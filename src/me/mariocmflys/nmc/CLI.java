package me.mariocmflys.nmc;

import me.mariocmflys.nmc.io.OutputConsole;

public class CLI implements OutputConsole {

	@Override
	public void write(String string, Type type) {
		if(type == Type.ERROR) {
			System.err.println(string);
			return;
		}
		System.out.println(string);
	}

	@Override
	public void write(Exception e, Type type) {
		e.printStackTrace();
	}
}
