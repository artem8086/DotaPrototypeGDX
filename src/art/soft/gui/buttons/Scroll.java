package art.soft.gui.buttons;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.gui.Interface;
import static art.soft.gui.buttons.IButton.curWin;

/**
 *
 * @author Artem
 */
public class Scroll extends TButton {

    public float speed;
    public int time, timeShow, wScroll;
    public int textY, oldX, oldY, poolH;
    private IButton but;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        wScroll *= xk;
    }

    @Override
    public void draw(Graphics g){
        if (textY>poolH - height) textY = poolH - height;
        if (textY<0) textY = 0;
        g.setClip(x, y, width, height);
        g.translate(0, -textY);
        drawElements(g);
        g.translate(0,  textY);
        if (time>0) {
            if (height!=0 && height<poolH) {
                float k = (float) poolH / height;
                g.fillRect(x+width-wScroll, y + (int) (textY / k), wScroll, (int) (height / k));
            }
            time -= Game.tFrame;
        }
        g.endClip();
    }

    public void drawElements(Graphics g){
        poolH = 0;
        int yt;
        if (buttons!=null)
        for (IButton ib : buttons) {
            poolH += ib.y;
            yt = ib.y;
            ib.y = y + poolH;
            ib.draw(g);
            ib.y = yt;
            poolH += ib.height;
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
                        p += but.y;
                        if ((b = but.onGUI(x, y + textY - this.y - p))!=null)
                            { this.but = b; return this; }
                        p += but.height;
                    }
                }
                return this;
            }
        }
        return null;
    }

    @Override
    public void pressed(int code, int k){
        if (but!=null) but.pressed(code, k);
        if (code==Interface.MOUSE_WHEEL_ROTATE) {
            textY += k * speed * Game.deltaT;
            time = timeShow;
            Interface.butClear = false;
        } else
        if (code==Interface.BUTTON_PRESSED) {
            Game.mWheel = 0;
            textY += oldY - curWin.mby;
            time = timeShow;
            Interface.butClear = false;
        }
        oldY = curWin.mby;
    }
}
