package art.soft.gui.buttons;

import art.soft.Game;
import art.soft.gui.Interface;
import static art.soft.gui.Interface.MOUSE_OVER;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import art.soft.utils.Image;
import art.soft.utils.Rectangle;

public class IButton extends Rectangle{

    public static Interface curWin;
    public boolean active = true;
    public IButton buttons[];
    public String Image;
    public Image im;
    public int key;

    public void load(double xk, double yk){
        if (Image!=null) {
            im = art.soft.utils.Image.loadImage(Image);
            Image = null;
        }
        x *= xk;
        width *= xk;
        y *= yk;
        height *= yk;
        if (buttons!=null)
        for (IButton but : buttons)
            but.load(xk, yk);
    }

    public void langReload(){
        if (buttons!=null)
        for (IButton but : buttons)
            but.langReload();
    }

    public void draw( art.soft.Graphics g){
        if (im!=null) {
            g.draw(im, x, y, width, height);
	}
        if (buttons!=null)
        for (IButton but : buttons)
            but.draw(g);
        TButton k = Game.HUD.keyBut;
        if (k!=null) {
            if (key!=0 && active) {
                int ke = Game.settings.keyCodes[key];
                k.setText(ke==0 ? "" : Keys.toString(ke));
                k.x = x - (k.width >> 1);
                k.y = y - (k.height >> 1);
                k.draw(g);
            }
        }
        if (Game.settings.butDebug) {
            if (curWin.selBut==this) {
                g.setColor(0xAAFFFF00);
                g.fillRect(x, y, width, height);
            }
            g.setColor(Color.RED);
            g.setStroke(1);
            g.drawRect(x, y, width, height);
        }
    }

    public boolean thisKeyPressed(){
        if (Game.keyPressed!=0) {
            if (key<0 || Game.keyPressed==Game.settings.keyCodes[key]) {
                keyPressed(Game.keyPressed);
                return true;
            }
        }
        if (Game.keyRelessed!=0 && (key<0 || Game.keyRelessed==Game.settings.keyCodes[key])) {
            keyRelessed(Game.keyRelessed);
            return true;
        }
        return false;
    }

    protected void keyPressed(int key) {
        pressed(Interface.BUTTON_PRESSED, key);
    }

    protected void keyRelessed(int key) {
        pressed(Interface.BUTTON_RELESSED, key);
    }

    public IButton onKey() {
        if (active) {
            if (thisKeyPressed()) return this;
            if (buttons!=null) {
                IButton b;
                for (IButton but : buttons) {
                    if ((b = but.onKey())!=null) return b;
                }
            }
        }
        return null;
    }

    public IButton onGUI(int x, int y){
        if (active) {
            if (contains(x, y)) {
                if (buttons!=null) {
                    IButton b;
                    for (IButton but : buttons) {
                        if ((b = but.onGUI(x, y))!=null) return b;
                    }
                }
                return this;
            }
        }
        return null;
    }

    public void pressed(int code, int k){
        if (code!=MOUSE_OVER) Game.statusKey = Game.DO_NOTHING;
    }

    public void dispose(){
        if (buttons!=null)
            for (IButton ib : buttons) ib.dispose();
        if (im!=null) im.dispose();
    }
}