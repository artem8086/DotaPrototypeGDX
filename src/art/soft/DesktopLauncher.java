package art.soft;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Dota-Prototype v1.2.6";
        config.addIcon(Game.dataDir+"icon.png", Files.FileType.Internal);
        config.fullscreen = true;
        config.resizable = false;
        //config.samples = 4;
        //GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        //DisplayMode dMode = myDevice.getDisplayMode();
        DisplayMode dMode = LwjglApplicationConfiguration.getDesktopDisplayMode();
        //config.vSyncEnabled = false;
        config.forceExit = true;
        config.width = dMode.width;//dMode.getWidth();
        config.height = dMode.height;//dMode.getHeight();
        config.depth = dMode.bitsPerPixel;//dMode.getBitDepth();

        new LwjglApplication(new Game(), config);
    }
}
