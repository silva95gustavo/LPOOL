package lpool.gdx.desktop;

import lpool.gui.GameProject;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	
	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = false; // TODO set to true
		config.samples = 2;
		new LwjglApplication(new GameProject(), config);
	}
}
