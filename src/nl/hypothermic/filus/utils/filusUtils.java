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
		String AB = "234567abcdefghijklmnopqrstuvwxyz";
		StringBuilder sb = new StringBuilder(len);
		for(int i = 0; i < len; i++) 
			sb.append(AB.charAt(random.nextInt(AB.length())));
		return sb.toString();
	}
	
	public static void tcNewIdentity() {
		try {
	        Socket socket = new Socket("localhost", 9052);
	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        out.println("AUTHENTICATE \"###uperUser999\"");
	        String xyz = in.readLine();
	        System.out.println("Authenticate answer: " + xyz);
	        if (xyz.contains("250")) {
	        	out.println("SIGNAL NEWNYM");
	        	System.out.println("NewNym answer: " + in.readLine());
	        } else {
	        	System.out.println("Nope." + xyz);
	        }
	        out.close();
	        in.close();
	        socket.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
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
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property fProxyTest-addr: " + e);
    			}
    			// Laad fProxyTest-enable
    			try {
    				int x = Integer.parseInt(props.getProperty("filusDebug"));
    				filusMain.propArray[9] = x;
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    				System.out.println("[F] NumberFormatException in property filusDebug: " + e);
    			}
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
    		props.setProperty("fProxyTest-addr", "www.hypothermic.nl");
    		props.setProperty("fThreads-count", "2");
    		props.setProperty("filusDebug", "1");
    		try {
    			propwrite = new FileWriter("filus.properties");
    			props.store(propwrite, "Filus Crawler by Hypothermic\nPlease note that fThreads-count is (still) hardcoded.\nhttps://github.com/hypothermic\nhttps://www.hypothermic.nl");
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
