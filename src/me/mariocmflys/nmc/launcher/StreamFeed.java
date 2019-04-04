package me.mariocmflys.nmc.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamFeed extends Thread {
	InputStream in;
	OutputConsole out;
	OutputConsole.Type type;
	
	public StreamFeed(InputStream in, OutputConsole out, OutputConsole.Type type) {
		this.in = in;
		this.out = out;
		this.type = type;
	}
	
	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			String ln = null;
			while ((ln = br.readLine()) != null) out.write(ln, type);
		}
		catch (IOException e) {
			System.out.println("STREAMFEED FATAL ERROR");
			e.printStackTrace();
		}
	}
	
}
