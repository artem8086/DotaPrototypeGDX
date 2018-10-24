package art.soft.gui;

import art.soft.Game;
import art.soft.utils.Image;
import art.soft.utils.TexturePacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SerializationException;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public class Settings {

    private static final int currentVersion = 3;

    private static final String settings_file = "settings.json";

    public final static int MINIFICATION_FILTER_KEY = 0;
    public final static int MAGNIFICATION_FILTER_KEY = 1;

    public int version;
    public String currentLang;
    public int keyCodes[];
    //public int mapRender[];

    public boolean showFPS, butDebug, showIntro;
    
    public boolean graphicsKeys[];


    public void reset(){
        showIntro = true;
        butDebug = showFPS = false;
        version = currentVersion;

        currentLang = Locale.getDefault().getISO3Language();
        //System.out.println("Current locale : "+locale);

        //mapRender = new int[keys.length];
        //mapRender[8] = 1;

        keyCodes = new int[23];
        keyCodes[1] = Keys.UP;
        keyCodes[2] = Keys.DOWN;
        keyCodes[3] = Keys.LEFT;
        keyCodes[4] = Keys.RIGHT;
        keyCodes[5] = Keys.F1;
        keyCodes[6] = Keys.A;
        keyCodes[7] = Keys.O;
        keyCodes[8] = Keys.Q;
        keyCodes[9] = Keys.W;
        keyCodes[10] = Keys.E;
        keyCodes[11] = Keys.R;
        keyCodes[12] = Keys.D;
        keyCodes[13] = Keys.F;
        keyCodes[14] = Keys.I;
        keyCodes[15] = Keys.F4;
        keyCodes[16] = Keys.Z;
        keyCodes[17] = Keys.X;
        keyCodes[18] = Keys.C;
        keyCodes[19] = Keys.V;
        keyCodes[20] = Keys.B;
        keyCodes[21] = Keys.N;
        keyCodes[22] = Keys.F6;
        
        // Графические ключи
        graphicsKeys = new boolean[2];
        graphicsKeys[0] = graphicsKeys[1] = true;
    }

    public static Settings Load(){
        Settings settings = null;
        FileHandle s = Gdx.files.local(settings_file);
        if (s.exists()) try {
            settings = Game.json.fromJson(Settings.class, s);
        } catch (SerializationException se) {}
        if (settings==null) {
            settings = new Settings();
            settings.reset();
        }
        if (settings.version!=currentVersion) settings.reset();

        //Game.settings = settings;
        //settings.apply();
        settings.keyCodes[0] = 0;
        return settings;
    }

    public void apply(){
        applyLanguage();
        applyRender();
    }

    public void applyLanguage(){
        currentLang = Game.language.getLocale(currentLang).locale;
        Game.language.curLangLoad();
    }

    public void setGraphicsKey(int key, boolean value) {
        if (key<graphicsKeys.length)
            graphicsKeys[key] = value;
    }

    public boolean getGraphicsKey(int key, boolean value) {
        if (key<graphicsKeys.length)
            return graphicsKeys[key];
        return false;
    }

    public void applyRender(){
        Image.applyTextureSetting();
        TexturePacks.applyTextureSetting();
        //for (int i = mapRender.length - 1; i>=0; i--) {
        //    Game.g.setRenderingHint(keys[i], values[mapRender[i] + (i << 1)]);
        //}
    }

    public static void Save(Settings settings){
        FileHandle s = Gdx.files.local(settings_file);
        Game.json.toJson(settings, s);
    }
}
