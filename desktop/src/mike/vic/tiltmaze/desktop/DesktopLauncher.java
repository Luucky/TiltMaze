package mike.vic.tiltmaze.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import mike.vic.tiltmaze.TiltGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Tilt Maze";
		//config.width = 600;
		//config.height = 480;
		new LwjglApplication(new TiltGame(), config);
	}
}
