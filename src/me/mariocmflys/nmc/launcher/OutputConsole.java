package me.mariocmflys.nmc.launcher;

import java.io.PrintStream;

public interface OutputConsole {
	
	/**
	 * Write a message to console
	 * @param string Message
	 * @param type Type of message
	 */
	public void write(String string, Type type);
	
	/**
	 * Write a stack trace to console
	 * @param e Exception
	 * @param type Type
	 */
	public void write(Exception e, Type type);
	
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
