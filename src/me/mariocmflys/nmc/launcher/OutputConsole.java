package me.mariocmflys.nmc.launcher;

import java.io.PrintStream;

public interface OutputConsole {
	public void write(String string, Type type);
	
	public enum Type {
		NORMAL(System.out),
		ERROR(System.err),
		INIT(System.out);
		
		private PrintStream stream;
		
		Type(PrintStream stream) {
			this.stream = stream;
		}
		
		public PrintStream getPrintStream() {
			return stream;
		}
	}
}
