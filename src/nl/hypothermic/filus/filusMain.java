package nl.hypothermic.filus;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import nl.hypothermic.filus.utils.filusUtils;
import nl.hypothermic.filus.websrv.filusWebMonitor;
import nl.hypothermic.filus.filusThread;

@SuppressWarnings("deprecation")

public class filusMain {
	
	/* Predefined vars: do NOT change */
	//comment:init bij elke loop*static ExecutorService filusThreadPool = Executors.newFixedThreadPool(10);
	public static List<String> foundOnions = new ArrayList<String>();
	public static int loopIteration = 0;
	public static int[] propArray = new int[32];
	public static String[] propArrayS = new String[32];
	private static String[] welcomeMsg = new String[]  {"<<<< Filus Crawler >>>>",
														"\nFor Tor Hidden Services",
														"\nCreated by Hypothermic.nl",
														"\nSee properties file for info"};
	/* Work with strings wherever possible instead of int's because they are easier to load from config! No booleans btw. */
	/* Test URL: deepdot35Wvmeyd5.onion */
	private static String rqHeader;
	private static String rqAgent;
	private static String torProxyAddr;
	private static int torProxyPort;
	private static String torControlPasswd = ""; 
	
	public static void main(String[] args) {
		filusUtils.initProps();
		if (propArray[0] == 1) {
			propArray[0] = 0;
			filusUtils.initProps();
		}
		rqHeader = propArrayS[4];
		rqAgent = propArrayS[3];
		torProxyAddr = propArrayS[2];
		torProxyPort = propArray[1];
		try {if (/*commented:not working inside IDE*(args[0] != null && args[0] == "debug") ||*/ propArray[9] == 1) {
			propArray[7] = 1;
			System.out.println("Debug mode activated, have fun!\nInitialized ints: " + Arrays.toString(propArray) + " \\nInitialized Strings: " + Arrays.toString(propArrayS));
		}} catch (Exception e) {
			// normale run - geen dbg mode
			propArray[7] = 2;
		}
		System.out.println(Arrays.toString(welcomeMsg).replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", ""));
		if (propArray[5] == 1) {
			//------------------------------fProxyTest------------------------------
			try {
				filusUtils.tcNewIdentity(torControlPasswd);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.out.println("[FSPT] Self-testing connection using fProxyTest. Addr: " + propArrayS[6]);
			try {
				boolean selfTestReachable = rqHandler(propArrayS[6]);
				if (selfTestReachable == true) {
					System.out.println("[FSPT] Self-testing: success!");
				} else {
					System.out.println("[FSPT] Self-testing: not reachable. Check your proxy config.");
					System.exit(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (propArray[13] == 1) {
			//------------------------------fControlTest------------------------------
			System.out.println("[FSCT] Self-testing Tor Control Port using fControlTest. Addr: " + propArrayS[14] + " Failover: " + propArrayS[15]);
				//main addr
				int sctMethod = 0;
				boolean sctMainAddrState = false;
				try {
					rqHandler(propArrayS[14]);
					sctMainAddrState = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (sctMainAddrState != false) {
					sctMethod = 1;
				} else {
					//then try bak addr
					boolean sctBakAddrState = false;
					try {
						rqHandler(propArrayS[15]);
						sctBakAddrState = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (sctBakAddrState != false) {
						sctMethod = 2;
					} else {
						System.out.println("[FSCT] fControlTest failed: could not reach main addr or failover addr.");
					}
				}
				if (sctMethod == 1) {
					try {
						int retryC = 0;
						boolean unluckyCollission = true;
						while (unluckyCollission == true && retryC < 3) {
							String bc = rqBody(propArrayS[14]);
							try {
								filusUtils.tcNewIdentity(torControlPasswd);
							} catch (Exception e) {
								e.printStackTrace();
							}
							TimeUnit.SECONDS.sleep(5);
							String ac = rqBody(propArrayS[14]);
							if (bc == ac) {
								// Maybe this is an unlucky collision (same exit node as previous), try again.
								retryC++;
							} else {
								// sct test is geslaagd.
								unluckyCollission = false;
								System.out.println("[FSCT] Self-test for Control Port has succeeded.");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("[FSCT] sct: Host unreachable");
						System.exit(1);
					}
				} else if (sctMethod == 2) {
					try {
						int retryC = 0;
						boolean unluckyCollission = true;
						while (unluckyCollission == true && retryC < 3) {
							String bc = rqBody(propArrayS[15]);
							try {
								filusUtils.tcNewIdentity(torControlPasswd);
							} catch (Exception e) {
								e.printStackTrace();
							}
							TimeUnit.SECONDS.sleep(5);
							String ac = rqBody(propArrayS[15]);
							if (bc == ac) {
								// Maybe this is an unlucky collision (same exit node as previous), try again.
								retryC++;
							} else {
								// sct test is geslaagd.
								unluckyCollission = false;
								System.out.println("[FSCT] Self-test for Control Port has succeeded. (note: bak address has been used!)");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("[FSCT] sct: Host unreachable");
						System.exit(1);
					}
				} else {
					System.out.println("[FSCT] Error: sctMethod assigned wrongly.");
				}
		}
		//------------------------------FilusWebMonitor------------------------------
		// TODO: testen van webserver 
	    Thread fwm = new Thread(new Runnable() {
	    public void run(){
	        	if (propArray[20] == 1) { try {
				System.out.println("[FWM] Filus Web Monitor is starting...");
				filusWebMonitor.main(null);
			} catch (IOException x) {
				x.printStackTrace();
			}}
	    }});  
	    fwm.start();
		//------------------------------FilusCrawler------------------------------
		System.out.println("[FILUS] Starting onion scanner.");
		loopIteration = 0;
		while (propArray[8] == 0) {
			// TODO: maak mechanisme om Filus te stoppen (aka verander propArray[8] naar !=0)\
			loopIteration++;
			System.out.println("[F] Executing new scan iteration: " + loopIteration);
			ExecutorService filusThreadPool = Executors.newFixedThreadPool(propArray[16]);
			for (int i = 0; i < propArray[16]; i++) {
				filusThreadPool.submit(new filusThread());
			}
			filusThreadPool.shutdown();
			try {
				// hardcoded omdat de threads zichzelf zullen terminaten, dus niet nodig.
				filusThreadPool.awaitTermination(12, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("Error: InterruptedException in awaiting threadPool termination");
			}
			try {
				filusUtils.tcNewIdentity(torControlPasswd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static boolean rqHandler(String addr) {
		try {
			rq(addr);
			return true;
		} catch (SocketException x) {
			String xmsg = x.toString();
			boolean xe = xmsg.contains("Connection refused: connect");
			if (xe == true) {
				System.out.println("> Error: Cannot reach the local Tor client proxy");
				if (loopIteration > 0) { System.out.println("[F] Info: if this happens frequently, try setting a greater value for the timeout between GET requests, setting fThreads-count to less, or if you have bridges enabled: turn off bridges."); }
				System.exit(1);
			}
			return false;
		} catch (SocketTimeoutException stx) {
			return false;
		} catch (ConnectTimeoutException ctx)  { 
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static void rq(String addr) throws Exception {
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create()
	            .register("http", new MyConnectionSocketFactory())
	            .register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault())).build();
	    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver());
	    CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).setConnectionTimeToLive(10, TimeUnit.SECONDS).build();
	    try {
	        InetSocketAddress socksaddr = new InetSocketAddress(torProxyAddr, torProxyPort);
	        HttpClientContext context = HttpClientContext.create();
	        context.setAttribute("socks.address", socksaddr);

	        RequestConfig rqConfig = RequestConfig.custom().setConnectTimeout(propArray[17]).build();
	        
	        HttpGet request = new HttpGet(addr);
	        request.setHeader(rqAgent, rqHeader);
	        request.setConfig(rqConfig);

	        if (propArray[9] == 1) { System.out.println("[F] LI" + loopIteration + " Debug: Executing request " + request + " via TORSOCKS5 proxy " + socksaddr); }
	        CloseableHttpResponse response = httpclient.execute(request, context);
	        try {
	            System.out.println("----------------------------------------");
	            System.out.println(response.getStatusLine());
	            int i = -1;
	            InputStream stream = response.getEntity().getContent();
	            while ((i = stream.read()) != -1) {
	                System.out.print((char) i);
	            }
	            EntityUtils.consume(response.getEntity());
	        } finally {
	            response.close();
	        }
	    } finally {
	        httpclient.close();
	    }
	}
	
	private static String rqBody(String addr) throws Exception {
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create()
	            .register("http", new MyConnectionSocketFactory())
	            .register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault())).build();
	    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver());
	    CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
	    try {
	        InetSocketAddress socksaddr = new InetSocketAddress(torProxyAddr, torProxyPort);
	        HttpClientContext context = HttpClientContext.create();
	        context.setAttribute("socks.address", socksaddr);

	        RequestConfig rqConfig = RequestConfig.custom().setConnectTimeout(propArray[18]).build();
	        
	        HttpGet request = new HttpGet(addr);
	        request.setHeader(rqAgent, rqHeader);
	        request.setConfig(rqConfig);
	        
	        // geen rq timeout want rqBody wordt alleen geexecuteerd als site bereikbaar is

	        System.out.println("Executing BODY request " + request + " via TORSOCKS5 proxy " + socksaddr);
	        CloseableHttpResponse response = httpclient.execute(request, context);
	        try {
	            System.out.println("----------------------------------------");
	            System.out.println(response.getStatusLine());
	            int i = -1;
	            InputStream stream = response.getEntity().getContent();
	            String xout = "";
	            while ((i = stream.read()) != -1) {
	                System.out.print((char) i);
	                xout = xout + Character.toString((char) i);
	            }
	            EntityUtils.consume(response.getEntity());
	            return xout.replaceAll("\n", "").replaceAll("\r", "");
	        } finally {
	            response.close();
	        }
	    } finally {
	        httpclient.close();
	    }
	}
	
	public static void setControlPasswd(String x) {
		torControlPasswd = x;
	}
}
class FakeDnsResolver implements DnsResolver {
    @Override
    public InetAddress[] resolve(String host) throws UnknownHostException {
        // Return some fake DNS record for every request, we won't be using it
        return new InetAddress[] { InetAddress.getByAddress(new byte[] { 1, 1, 1, 1 }) };
    }
}

class MyConnectionSocketFactory extends PlainConnectionSocketFactory {
    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
        return new Socket(proxy);
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
            InetSocketAddress localAddress, HttpContext context) throws IOException {
        // Convert address to unresolved
        InetSocketAddress unresolvedRemote = InetSocketAddress
                .createUnresolved(host.getHostName(), remoteAddress.getPort());
        return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context);
    }
}

class MySSLConnectionSocketFactory extends SSLConnectionSocketFactory {

    @SuppressWarnings("deprecation") //warning maakt niet uit omdat 'deprecated' dingen nogsteeds werken :P
	public MySSLConnectionSocketFactory(final SSLContext sslContext) {
        // You may need this verifier if target site's certificate is not secure
        super(sslContext, ALLOW_ALL_HOSTNAME_VERIFIER);
    }

    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
        return new Socket(proxy);
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
            InetSocketAddress localAddress, HttpContext context) throws IOException {
        // Convert address to unresolved
        InetSocketAddress unresolvedRemote = InetSocketAddress
                .createUnresolved(host.getHostName(), remoteAddress.getPort());
        return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context);
    }
}
