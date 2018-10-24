package art.soft.gui.buttons.menu;

import art.soft.gui.Interface;
import static art.soft.gui.buttons.IButton.curWin;
import art.soft.gui.buttons.TButton;

/**
 *
 * @author Artem
 */
public class BOpenW extends TButton {

    public int win[];
    public boolean mover = false;
    public int color1, color2;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        color1 = color;
    }

    @Override
    public void draw(art.soft.Graphics g){
        if (mover) color = color2;
        super.draw(g);
        if (buttons!=null) buttons[0].im = null;
        color = color1;
        mover = false;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            curWin.hideWin();
            Interface w = Interface.getWindow(win);
            if (w!=null) w.openWin();
        }
    }
}
