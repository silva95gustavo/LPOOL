package lpool.gdx.desktop;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import lpool.gui.GameProject;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	
	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = false;
		new LwjglApplication(new GameProject(), config);
	}
}
