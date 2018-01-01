package nl.hypothermic.filus;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
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

@SuppressWarnings("deprecation")

public class filusMain {
	
	/* Predefined vars: do NOT change */
	static filusThread ft1 = new filusThread();
	static filusThread ft2 = new filusThread();
	public static int[] propArray = new int[16];
	public static String[] propArrayS = new String[16];
	private static String[] welcomeMsg = new String[]  {"<<<< Filus Crawler >>>>",
														"\nFor Tor Hidden Services",
														"\nCreated by Hypothermic.nl"};
	/* Work with strings wherever possible instead of int's because they are easier to load from config! */
	/* Test URL: deepdot35Wvmeyd5.onion */
	private static String rqHeader = propArrayS[4];
	private static String rqAgent = propArrayS[3];
	private static String torProxyAddr = propArrayS[2];
	private static int torProxyPort = propArray[1];

	Thread crawl = new Thread(new Runnable() {
	    public void run()
	    {
	         // Crawler
	    }});  
	
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
		System.out.println(torProxyAddr);
		try {if (/*(args[0] != null && args[0] == "debug") ||*/ propArray[9] == 1) {
			propArray[7] = 1;
			System.out.println("Debug mode activated, have fun!\nInitialized ints: " + Arrays.toString(propArray) + " \\nInitialized Strings: " + Arrays.toString(propArrayS));
		}} catch (Exception e) {
			// normale run - geen dbg mode
		}
		System.out.println(Arrays.toString(welcomeMsg).replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", ""));
		if (propArray[5] == 1) {
			System.out.println("Self-testing connection using fProxyTest. Addr: " + propArrayS[6]);
			try {
				boolean selfTestReachable = rqHandler(propArrayS[6]);
				if (selfTestReachable == true) {
					System.out.println("Self-testing: success!");
				} else {
					System.out.println("Self-testing: not reachable. Check your proxy config.");
					System.exit(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*while (propArray[8] == 0) {
			// exit if proparray[8] != 0
			ft1.start();
			ft2.start();
			try {
				waitForThreads();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("Error: InterruptedException: Did something stop the Filus Threads?");
				System.exit(1);
			}
		}*/
	}
	
	public static void waitForThreads() throws InterruptedException {
		ft1.join();
		ft2.join();
	}
	
	public static boolean rqHandler(String addr) {
		try {
			rq(addr);
			return true;
		} catch (SocketException x) {
			String xmsg = x.toString();
			boolean xe = xmsg.contains("Connection refused: connect");
			if (xe = true) {
				System.out.println("Cannot reach the local Tor client proxy");
			}
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private static void rq(String addr) throws Exception {
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create()
	            .register("http", new MyConnectionSocketFactory())
	            .register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault())).build();
	    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver());
	    CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
	    try {
	        InetSocketAddress socksaddr = new InetSocketAddress(torProxyAddr, torProxyPort);
	        HttpClientContext context = HttpClientContext.create();
	        context.setAttribute("socks.address", socksaddr);

	        HttpGet request = new HttpGet(addr);
	        request.setHeader(rqAgent, rqHeader);

	        System.out.println("Executing request " + request + " via SOCKS proxy " + socksaddr);
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
