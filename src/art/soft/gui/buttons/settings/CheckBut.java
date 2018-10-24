package art.soft.gui.buttons.settings;

import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class CheckBut extends IButton {

    private static Image check_on, check_off, check_over;
    public String off, over;
    public boolean flag, mover;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        if (im!=null) {
            check_on = im;
            im = null;
        }
        if (off!=null) {
            check_off = art.soft.utils.Image.loadImage(off);
            off = null;
        }
        if (over!=null) {
            check_over = art.soft.utils.Image.loadImage(over);
            over = null;
        }
    }

    @Override
    public void draw(art.soft.Graphics g){
        if (flag) im = check_on;
        else im = mover ? check_over : check_off;
        super.draw(g);
        mover = false;
        im = null;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            flag ^= true;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (check_on!=null) { check_on.dispose(); check_on = null; }
        if (check_off!=null) { check_off.dispose(); check_off = null; }
        if (check_over!=null) { check_over.dispose(); check_over = null; }
    }
}
