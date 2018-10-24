package art.soft.gui;

import art.soft.utils.Fonts;
import art.soft.Game;
import java.util.HashMap;

/**
 *
 * @author Artem
 */
public class Language {

    public static inGameWords words;
    public static int curLangNum;

    public inGameWords keyWords;
    public Locale[] locales;

    public HashMap<String, String> text; // = new HashMap<String, String>();

    public static Language Load(){
        //words = new inGameWords();
        Language l = Game.json.fromJson(Language.class, Game.openFile("locales/langs.json"));
        words = l.keyWords;
        return l;
    }

    public Locale getLocale(String locale){
        for (int i = 0; i<locales.length; i++) {
            if (locales[i].locale.equals(locale)) {
                curLangNum = i;
                return locales[i];
            }
        }
        return locales[0];
    }

    public void curLangLoad(){
        Language l = Game.json.fromJson(Language.class, Game.openFile(
            getLocale(Game.settings.currentLang).path));
        text = l.text;
        String oldFont = words.font;
        if (Game.mainMap!=null) Game.mainMap.loadLang();
        words.Load();

        if (oldFont==null || !oldFont.equals(words.font)) {
            if (Game.fonts!=null) {
                Game.fonts.dispose();
            }
            Game.fonts = Game.json.fromJson(Fonts.class, Game.openFile(words.font));
            Game.fonts.Load();
            Game.g.setFont(Game.getDefaultFont());
        }
    }

    public String getText(String key){
        if (Game.mapInfo!=null) {
            Language l = Game.mapInfo.mapLang;
            if (l!=null && l.text.containsKey(key))
                return l.text.get(key);
        }
        return text.containsKey(key) ? text.get(key) : key;
    }
}
