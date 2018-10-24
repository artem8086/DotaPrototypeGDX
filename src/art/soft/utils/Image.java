package art.soft.utils;

import art.soft.Game;
import art.soft.gui.Settings;
import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
import static com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;

/**
 *
 * @author Artem
 */
public class Image {

    private static final HashMap<String, Image> allImages = new HashMap<String, Image>();

    private String path;
    public Texture texture;
    public TextureRegion textureRegion;
    

    public Image(String file) {
        texture = new Texture(Gdx.files.internal(Game.dataDir + file));
        path = file;
    }

    public Image(TextureRegion tReg) {
        textureRegion = tReg;
    }

    public static Image loadImage(String im) {
        Image i;
        if (im.charAt(0)=='@') {
            i = new Image(TexturePacks.getTextureRegion(im));
        } else {
            if (allImages.containsKey(im)) {
                return allImages.get(im);
            }
            i = new Image(im);
            i.setFilters();
            allImages.put(im, i);
        }
        return i;
    }

    public void setFilters() {
        boolean keys[] = Game.settings.graphicsKeys;
        texture.setFilter(keys[Settings.MINIFICATION_FILTER_KEY] ? Linear : Nearest,
                keys[Settings.MAGNIFICATION_FILTER_KEY] ? Linear : Nearest);
    }

    public int getWidth() {
        return texture.getWidth();
    }

    public int getHeight() {
        return texture.getHeight();
    }

    public String getPath(){
        return path;
    }

    public void dispose() {
        if (path!=null) allImages.remove(path);
        if (texture!=null) texture.dispose();
    }

    private void sDispose() {
        if (texture!=null) texture.dispose();
    }

    public static void applyTextureSetting() {
        TextureFilter miniF = Game.settings.graphicsKeys[Settings.MINIFICATION_FILTER_KEY] ? Linear : Nearest;
        TextureFilter magF = Game.settings.graphicsKeys[Settings.MAGNIFICATION_FILTER_KEY] ? Linear : Nearest;
        for (Image i : allImages.values()) {
            if (i.texture!=null) i.texture.setFilter(miniF, magF);
        }
    }

    public static void disposeAll() {
        for (Image i : allImages.values()) {
            i.sDispose();
        }
        allImages.clear();
    }
}
