package art.soft.gui.buttons.settings;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.Language;
import art.soft.gui.Locale;
import art.soft.gui.buttons.TButton;

/**
 *
 * @author Artem
 */
public class LangBut extends TButton {

    public boolean mover = false;
    public Locale locale;
    public int n;
    private static final int col1 = 0x404040;
    private static final int col2 = 0x202020;
    private static final int col3 = 0x1111CC;

    public static LangBut createBut(int x, int y, int w, int h, int fontSize, boolean bold, int c, Locale loc, int n){
        LangBut lb = new LangBut();
        lb.x = x; lb.y = y;
        lb.width = w; lb.height = h;
        lb.color = c;
        lb.fontSize = fontSize;
        lb.bold = bold;
        lb.locale = loc;
        lb.n = n;
        lb.langReload();
        return lb;
    }

    @Override
    public void langReload(){
        keyWord = locale.getName(Language.curLangNum);
        super.langReload();
    }

    @Override
    public void draw(art.soft.Graphics g){
        if (Language.curLangNum!=n) {
            g.setColor(mover ? col1 : col2);
            active = true;
        } else {
            g.setColor(col3);
            active = false;
        }
        g.fillRect(x, y, width, height);
        super.draw(g);
        mover = false;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            Game.settings.currentLang = locale.locale;
            Game.reset = Game.RESET_LANGUAGE;
        }
    }
}
