package com.killmonster;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.killmonster.KillMonster;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("KillMonster");
		config.useVsync(true);
		config.setWindowedMode(26*16*3, 14*16*3);;
		new Lwjgl3Application(new KillMonster(), config);
	}
}
