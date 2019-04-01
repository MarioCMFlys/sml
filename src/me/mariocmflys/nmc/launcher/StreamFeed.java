package me.mariocmflys.nmc.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class StreamFeed extends Thread {
	InputStream in;
	PrintStream out;
	
	public StreamFeed(InputStream in, PrintStream out) {
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			String ln = null;
			while ((ln = br.readLine()) != null) out.println("> " + ln);
		}
		catch (IOException e) {
			System.out.println("STREAMFEED FATAL ERROR");
			e.printStackTrace();
		}
	}
	
}
