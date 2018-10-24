package art.soft.utils;

import art.soft.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;

/**
 *
 * @author Artem
 */
public class Fonts {

    private static final String FONT_DIR = "fonts/";
    private static final float HEIGHT_FOR_FONT = 768;

    public int fontSize;
    public boolean bold;
    public String path;
    public BitmapFont font;

    public Fonts fonts[];

    public void Load() {
        for (Fonts f : fonts) {
            if (f.path!=null) {
                f.font = new BitmapFont(Game.openFile(FONT_DIR+f.path+".fnt"));
                BitmapFontData d = f.font.getData();
                d.setScale((float) Game.h / HEIGHT_FOR_FONT);
                d.markupEnabled = true;
                f.path = null;
            }
        }
        font = fonts[0].font;
    }

    public void dispose() {
        for (Fonts f : fonts) {
            f.font.dispose();
        }
    }
}
