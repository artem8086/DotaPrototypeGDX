package art.soft.gui.buttons.console;

import art.soft.Game;
import art.soft.gui.buttons.IButton;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class butBackground extends IButton {

    public static butBackground instance;
    public float opacity = 1, startOp, speedOp;
    private Image im1, im2, set;
    public String onImg;

    @Override
    public void load(double xk, double yk){
        instance = this;
        super.load(xk, yk);
        im1 = im;
        if (onImg!=null) {
            set = im2 = art.soft.utils.Image.loadImage(onImg);
            onImg = null;
        }
    }

    @Override
    public void draw( art.soft.Graphics g){
        if (opacity<1) {
            g.draw(im, x, y, width, height);
            g.setAlpha(opacity);
            g.draw(set, x, y, width, height);
            g.setAlphaNormal();
            opacity += speedOp * Game.deltaT;
        } else {
            g.draw(im, x, y, width, height);
            opacity = 1;
        }
        //img = topbar;
    }

    public void setMouseOver(boolean on){
        Image i = on ? im1 : im2;
        if (i!=null && i!=set) {
            im = set;
            set = i;
            opacity = startOp;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (im1!=null) im1.dispose();
        if (im2!=null) im2.dispose();
        instance = null;
    }
}
