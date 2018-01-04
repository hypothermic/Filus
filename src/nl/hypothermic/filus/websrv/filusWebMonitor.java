package nl.hypothermic.filus.websrv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import nl.hypothermic.filus.filusMain;
 
public class filusWebMonitor {
 
    private static final Executor fThreadPool = Executors.newFixedThreadPool(100);
 
    public static void main(String[] args) throws IOException {
    	InetAddress addr = InetAddress.getByName(filusMain.propArrayS[22]);
        try {/*BEGIN*/ ServerSocket socket = new ServerSocket(filusMain.propArray[21], 0, addr);
        System.out.println("[FWM] Started Filus Web Monitor on: " + filusMain.propArrayS[22] + ":" + filusMain.propArray[21]);
        while (true) {
            final Socket connection = socket.accept();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    HandleRequest(connection);
                }
            };
            fThreadPool.execute(task);
        }/*EINDE*/} catch (Exception e) { System.out.println("[FWM] Webmonitor error: Exception: " + e); }
    }
 
    private static void HandleRequest(Socket s) {
        BufferedReader in;
        PrintWriter out;
        String request;
        try {
            String remote = s.getInetAddress().toString();
            System.out.println("[FWM] " + remote + " has connected to FilusWebMonitor.");
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            request = in.readLine();
 
            out = new PrintWriter(s.getOutputStream(), true);
            out.println("HTTP/1.0 200");
            out.println("Content-type: text/html");
            out.println("Server-name: myserver");
            String response;
            if (filusMain.foundOnions.isEmpty()) {
            	response = "<html>"
            			+ "<head><title>My Web Server</title></head><body><center>"
            			+ "<h1>Filus Web Monitor</h1>"
            			+ "v1.5 - https://github.com/hypothermic/filus"
            			+ "<br><br><h3>Found onions:</h3>"
            			+ "No hidden services have been found yet. Check back later."
            			+ "<br><br><h3>Filus configuration:</h3>"
            			+ "User Agent: " + filusMain.propArrayS[3]
            			+ "Request Header: " + filusMain.propArrayS[4]
            			+ "Tor Address: " + filusMain.propArrayS[2] + filusMain.propArray[1]
            			+ "</body></center></html>";
            } else {
            	response = "<html>"
                        + "<head><title>My Web Server</title></head><body><center>"
                        + "<h1>Filus Web Monitor</h1>"
                        + "v1.5 - https://github.com/hypothermic/filus"
                        + "<br><br><h3>Found onions:</h3>"
                        + filusMain.foundOnions.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(",", "<br><b>&</b>")
            			+ "<br><br><h3>Filus configuration:</h3>"
            			+ "<b>User Agent:</b> " + filusMain.propArrayS[3]
            			+ "<br><b>Request Header:</b> " + filusMain.propArrayS[4]
            			+ "<br><b>Tor Address:</b> " + filusMain.propArrayS[2] + filusMain.propArray[1]
                        + "</body></center></html>";
            }
            out.println("Content-length: " + response.length());
            out.println("");
            out.println(response);
            out.flush();
            out.close();
            s.close();
        }
        catch (IOException e) {
            System.out.println("Failed respond to client request: " + e.getMessage());
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;
    }
 
}

