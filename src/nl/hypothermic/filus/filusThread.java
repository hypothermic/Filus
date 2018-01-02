package nl.hypothermic.filus;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import nl.hypothermic.filus.utils.filusUtils;

public class filusThread implements Runnable {

	public void run() {
		String x = "http://" + filusUtils.randomString(16) + ".onion";
		boolean xstatus = filusMain.rqHandler(x);
		if (xstatus == true) { try {
			Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			PrintWriter z = new PrintWriter("onions-found.md", "UTF-8");
			z.println("[FILUS] " + sdf.format(cal.getTime()) + " Found a hidden service: " + x);
			z.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// zou niet mogen gebeuren, hopelijk "unreachable code" hehe
			e.printStackTrace();
		}}
	}
}
