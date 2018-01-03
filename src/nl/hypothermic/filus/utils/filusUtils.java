package nl.hypothermic.filus.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Properties;

import nl.hypothermic.filus.filusMain;

public class filusUtils {

	static SecureRandom random = new SecureRandom();
	
	public static String randomString(int len) {
		// om base32/rfc adres te bouwen
		String AB = "234567abcdefghijklmnopqrstuvwxyz";
		StringBuilder sb = new StringBuilder(len);
		for(int i = 0; i < len; i++) 
			sb.append(AB.charAt(random.nextInt(AB.length())));
		return sb.toString();
	}
	
	public static void tcNewIdentity(String controlPasswd) throws Exception {
		// om identiteit te veranderen (nieuw IP addr)
        Socket socket = new Socket(filusMain.propArrayS[11], filusMain.propArray[10]);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println("AUTHENTICATE \"" + controlPasswd + "\"");
        String xyz = in.readLine();
        System.out.println("Authenticate answer: " + xyz);
        if (xyz.contains("250")) {
        	out.println("SIGNAL NEWNYM");
       		System.out.println("NewNym answer: " + in.readLine());
        } else if (xyz.contains("250")) {
        	System.out.println("Error: wrong password!!");
        } else {
	        System.out.println("Nope." + xyz);
	        throw new Exception();
        }
        out.close();
        in.close();
        socket.close();
	}
	
	public static void initProps() {
		/* Toegewezen array plekken aan properties:
		 * 0 - first run (niet in cfg)
		 * 1 - torProxy-port
		 * 2 - torProxy-addr
		 * 3 - rqAgent
		 * 4 - rqHeader
		 * 5 - fProxyTest-enable
		 * 6 - fProxyTest-addr
		 * 7 - debug mode (niet in cfg)
		 * 8 - exited (niet in cfg)
		 * 9 - debug via config
		 * 10 - torControl-port
		 * 11 - torControl-addr
		 * 12 - torControl-passwd (zie warning: unhashed passwd) [Removed, now private with setter!]
		 * 13 - fControlTest-enable
		 * 14 - fControlTest-addr
		 * 15 - fControlTest-failover (ook "bak addr" genoemd, te lui om te renamen lol.)
		 * 16 - fThreads-count
		 * 17 - fConnTimeout
		 * 18 - fControlTest-connTimeout
		 * 19 - fProxyTest-connTimeout
		 */
    	File propsExist = new File("filus.properties");
    	if (propsExist.exists() && !propsExist.isDirectory()) {
    		try (FileReader reader = new FileReader("filus.properties")) {
    			Properties props = new Properties();
    			props.load(reader);
    			// Laad torProxy-port
    			try {
    				int x = Integer.parseInt(props.getProperty("torProxy-port"));
    				filusMain.propArray[1] = x;
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property torProxy-port: " + e);
    			}
    			// Laad torProxy-addr
    			try {
    				filusMain.propArrayS[2] = props.getProperty("torProxy-addr");
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property torProxy-addr: " + e);
    			}
    			// Laad rqAgent
    			try {
    				filusMain.propArrayS[3] = props.getProperty("rqAgent");
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property rgAgent: " + e);
    			}
    			// Laad rqHeader
    			try {
    				filusMain.propArrayS[4] = props.getProperty("rqHeader");
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property rqHeader: " + e);
    			}
    			// Laad fProxyTest-enable
    			try {
    				int x = Integer.parseInt(props.getProperty("fProxyTest-enable"));
    				filusMain.propArray[5] = x;
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property fProxyTest-enable: " + e);
    			}
    			// Laad fProxyTest-addr
    			try {
    				filusMain.propArrayS[6] = props.getProperty("fProxyTest-addr");
    			} catch (Exception e) {
    				e.printStackTrace();
    				System.out.println("[F] Exception in property fProxyTest-addr: " + e);
    			}
    			// Laad fProxyTest-enable
    			try {
    				int x = Integer.parseInt(props.getProperty("filusDebug"));
    				filusMain.propArray[9] = x;
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property filusDebug: " + e);
    			}
    			// Laad torControl-port
    			try {
    				int x = Integer.parseInt(props.getProperty("torControl-port"));
    				filusMain.propArray[10] = x;
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property torControl-port: " + e);
    			}
    			// Laad torControl-addr
    			try {
    				filusMain.propArrayS[11] = props.getProperty("torControl-addr");
    			} catch (Exception e) {
    				e.printStackTrace();
    				System.out.println("[F] Exception in property torControl-addr: " + e);
    			}
    			// Laad torControl-passwd (warning: plaintext!!, kan niet met hashed pw helaas.)
    			try {
    				filusMain.setControlPasswd(props.getProperty("torControl-passwd"));
    			} catch (Exception e) {
    				e.printStackTrace();
    				System.out.println("[F] Exception in property torControl-passwd: " + e);
    			}
    			// Laad fControlTest-enable
    			try {
    				int x = Integer.parseInt(props.getProperty("fControlTest-enable"));
    				filusMain.propArray[13] = x;
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property fControlTest-enable: " + e);
    			}
    			// Laad fControlTest-addr
    			try {
    				filusMain.propArrayS[14] = props.getProperty("fControlTest-addr");
    			} catch (Exception e) {
    				e.printStackTrace();
    				System.out.println("[F] Exception in property fControlTest-addr: " + e);
    			}
    			// Laad fControlTest-addr
    			try {
    				filusMain.propArrayS[15] = props.getProperty("fControlTest-addr-failover");
    			} catch (Exception e) {
    				e.printStackTrace();
    				System.out.println("[F] Exception in property fControlTest-addr-failover: " + e);
    			}
    			// Laad fThreads-count
    			try {
    				int x = Integer.parseInt(props.getProperty("fThreads-count"));
    				filusMain.propArray[16] = x;
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property fThreads-count: " + e);
    			}
    			// Laad fConnTimeout
    			try {
    				int x = Integer.parseInt(props.getProperty("fConnTimeout"));
    				filusMain.propArray[17] = x;
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property fConnTimeout: " + e);
    			}
    			if (filusMain.propArray[13] == 1) { // Laad fControlTest-connTimeout als hij enabled staat.
    			try {
    				int x = Integer.parseInt(props.getProperty("fControlTest-connTimeout"));
    				filusMain.propArray[18] = x;
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property fControlTest-connTimeout: " + e);
    			}}
    			if (filusMain.propArray[5] == 1) { // Laad fProxyTest-connTimeout als hij enabled staat.
    			try {
    				int x = Integer.parseInt(props.getProperty("fProxyTest-connTimeout"));
    				filusMain.propArray[19] = x;
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property fProxy-connTimeout: " + e);
    			}}

    		} catch (Exception x3) {
    			x3.printStackTrace();
    		}
    	} else {
    		// Set first-run && mk props file
    		filusMain.propArray[0] = 1;
    		FileWriter propwrite = null;
    		Properties props = new Properties();
    		props.setProperty("torProxy-port", "9052");
    		props.setProperty("torProxy-addr", "127.0.0.1");
    		props.setProperty("rqAgent", "MyUserAgent");
    		props.setProperty("rqHeader", "MyHeader");
    		props.setProperty("fProxyTest-enable", "1");
    		props.setProperty("fProxyTest-addr", "http://www.neverssl.com");
    		props.setProperty("fThreads-count", "8");
    		props.setProperty("filusDebug", "1");
    		props.setProperty("torControl-port", "9053");
    		props.setProperty("torControl-addr", "127.0.0.1");
    		props.setProperty("torControl-passwd", "MyGreatPassword");
    		props.setProperty("fControlTest-enable", "1");
    		props.setProperty("fControlTest-addr", "http://checkip.amazonaws.com/");
    		props.setProperty("fControlTest-addr-failover", "http://icanhazip.com/");
    		props.setProperty("fConnTimeout", "15000");
    		props.setProperty("fControlTest-connTimeout", "30000");
    		props.setProperty("fProxyTest-connTimeout", "30000");
    		try {
    			propwrite = new FileWriter("filus.properties");
    			props.store(propwrite, "Filus Crawler by Hypothermic\nMore info about this file in README.md\nSet control passwd in plain text, and restrict reading this file to root and the java user!!\nPlease do not enable fControlTest if fProxyTest is disabled.\nhttps://github.com/hypothermic\nhttps://www.hypothermic.nl");
    			propwrite.close();
    		} catch (IOException x1) {
    			x1.printStackTrace();
    			System.out.println("[F] I/O Exception: " + x1 +  " \n");
    		} finally {
    			if (propwrite != null) {
    				try {
    					propwrite.close();
    				} catch (Exception x2) {
    					x2.printStackTrace();
    				}
    			}
    		}
    	}
    }
}
