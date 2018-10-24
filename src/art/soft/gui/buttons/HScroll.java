package art.soft.gui.buttons;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.gui.Interface;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Artem
 */
public class HScroll extends TButton {

    public float speed;
    public int time, timeShow, wScroll;
    public int textY, oldY, poolH;
    public boolean mover;
    private IButton but, pressed;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        wScroll *= xk;
    }

    @Override
    public void draw(Graphics g) {
        if (!mover) oldY = Game.ym;
        if (textY>poolH - height) textY = poolH - height;
        if (textY<0) textY = 0;
        g.setClip(x-1, y-1, width+2, height+2);
        g.translate(0, -textY);
        drawElements(g);
        g.endClip();
        g.translate(0,  textY);
        if (time>0) {
            if (height!=0 && height<poolH) {
                float k = (float) poolH / height;
                g.setColor(color);
                g.fillRect(x+width-wScroll, y + (int) (textY / k), wScroll, (int) (height / k));
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
        poolH = 0;
        int yt;
        if (buttons!=null)
        for (IButton ib : buttons) {
            g.translate(0, y + poolH);
            ib.draw(g);
            g.translate(0, - y - poolH);
            poolH += ib.height + ib.y;
        }
    }

    @Override
    public IButton onGUI(int x, int y){
        if (active) {
            but = null;
            if (contains(x, y)) {
                int yt;
                if (buttons!=null) {
                    int p = 0;
                    IButton b;
                    for (IButton but : buttons) {
                        if ((b = but.onGUI(x, y + textY - this.y - p))!=null)
                            { this.but = b; return this; }
                        p += but.height + but.y;
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
        mover = true;
        if (but!=null) {
            if (but!=pressed) but.pressed(code, k);
            if (code!=Interface.MOUSE_OVER) {
                if (Interface.butClear) pressed = but;
            } else pressed = null;
        }
        if (code==Interface.MOUSE_WHEEL_ROTATE) {
            textY += k * speed * Game.deltaT;
            time = timeShow;
            Interface.butClear = false;
        } else
        if (code==Interface.BUTTON_PRESSED) {
            Game.mWheel = 0;
            textY += oldY - Game.ym;
            time = timeShow;
            Interface.butClear = false;
        }
        oldY = Game.ym;
    }
}
