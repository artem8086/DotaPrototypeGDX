package art.soft.gui.buttons;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.gui.Interface;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Artem
 */
public class WScroll extends TButton {

    public float speed;
    public int time, timeShow, wScroll;
    public int textX, oldX, poolW;
    public boolean mover;
    public IButton but, pressed;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        wScroll *= xk;
    }

    @Override
    public void draw(Graphics g){
        if (!mover) oldX = Game.xm;
        if (textX>poolW - width) textX = poolW - width;
        if (textX<0) textX = 0;
        g.setClip(x-1, y-1, width+2, height+2);
        g.translate(-textX, 0);
        drawElements(g);
        g.endClip();
        g.translate( textX, 0);
        if (time>0) {
            if (width!=0 && width<poolW) {
                float k = (float) poolW / width;
                g.setColor(color);
                g.fillRect(x + (int) (textX / k), y+height-wScroll, (int) (width / k), wScroll);
            }
            time -= Game.tFrame;
        }
        if (Game.settings.butDebug) {
            g.setColor(Color.RED);
            g.setStroke(1);
            g.drawRect(x, y, width, height);
        }
        mover = false;
    }

    public void drawText(Graphics g) {
        IButton b[] = buttons;
        buttons = null;
        super.draw(g);
        buttons = b;
    }

    public void drawElements(Graphics g){
        poolW = 0;
        int yt;
        if (buttons!=null)
        for (IButton ib : buttons) {
            g.translate(x + poolW, 0);
            ib.draw(g);
            g.translate(- x - poolW, 0);
            poolW += ib.width + ib.x;
        }
    }

    @Override
    public IButton onGUI(int x, int y){
        mover = true;
        if (active) {
            but = null;
            if (contains(x, y)) {
                int yt;
                if (buttons!=null) {
                    int p = 0;
                    IButton b;
                    for (IButton but : buttons) {
                        if ((b = but.onGUI(x + textX - this.x - p, y))!=null)
                            { this.but = b; return this; }
                        p += but.width + but.x;
                    }
                }
                pressed = null;
                return this;
            }
        }
        pressed = null;
        return null;
    }

    @Override
    public void pressed(int code, int k){
        if (but!=null) {
            if (but!=pressed) but.pressed(code, k);
            if (code!=Interface.MOUSE_OVER) {
                if (Interface.butClear) pressed = but;
            } else pressed = null;
        }
        if (code==Interface.MOUSE_WHEEL_ROTATE) {
            textX += k * speed * Game.deltaT;
            time = timeShow;
            Interface.butClear = false;
        } else
        if (code==Interface.BUTTON_PRESSED) {
            Game.mWheel = 0;
            textX += oldX - Game.xm;
            time = timeShow;
            Interface.butClear = false;
        }
        oldX = Game.xm;
    }
}
