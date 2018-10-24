package art.soft.utils;

import art.soft.Game;
import art.soft.gui.Settings;
import com.badlogic.gdx.graphics.Texture;
import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
import static com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;

/**
 *
 * @author Artem
 */
public class TexturePacks {

    private static final HashMap<String, TextureAtlas> allAtlas = new HashMap<String, TextureAtlas>();

    public String name, path;

    public void Load() {
        if (!allAtlas.containsKey(name)) {
            TextureAtlas ta = new TextureAtlas(Game.openFile(path));
                // устанавливаем фильтры текстур
            Texture.TextureFilter miniF = Game.settings.graphicsKeys[Settings.MINIFICATION_FILTER_KEY] ? Linear : Nearest;
            Texture.TextureFilter magF = Game.settings.graphicsKeys[Settings.MAGNIFICATION_FILTER_KEY] ? Linear : Nearest;
            for (Texture t : ta.getTextures()) {
                t.setFilter(miniF, magF);
            }
            allAtlas.put(name, ta);
        }
        path = null;
    }

    public static TextureRegion getTextureRegion(String path) {
        String[] index = path.split("@");
        TextureAtlas ta = allAtlas.get(index[1]);
        if (ta!=null) {
            return ta.findRegion(index[2]);
        }
        return null;
    }

    public void dispose() {
        TextureAtlas ta = allAtlas.remove(name);
        if (ta!=null) ta.dispose();
    }

    public static void applyTextureSetting() {
        Texture.TextureFilter miniF = Game.settings.graphicsKeys[Settings.MINIFICATION_FILTER_KEY] ? Linear : Nearest;
        Texture.TextureFilter magF = Game.settings.graphicsKeys[Settings.MAGNIFICATION_FILTER_KEY] ? Linear : Nearest;
        for (TextureAtlas ta : allAtlas.values()) {
            for (Texture t : ta.getTextures()) {
                t.setFilter(miniF, magF);
            }
        }
    }

    public static void disposeAll() {
        for (TextureAtlas ta : allAtlas.values()) {
            ta.dispose();
        }
        allAtlas.clear();
    }
}
