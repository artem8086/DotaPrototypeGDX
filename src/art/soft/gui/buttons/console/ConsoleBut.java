package art.soft.gui.buttons.console;

import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class ConsoleBut extends IButton {

    public String onImg, hover;
    public Image im1, im2, im3;
    //public int win[];
    //public static Interface console;
    public boolean console;
    public boolean mover = false;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        //console = Interface.getWindow(win);
        im2 = im;
        if (onImg!=null) {
            im1 = art.soft.utils.Image.loadImage(onImg);
            onImg = null;
        }
        if (hover!=null) {
            im3 = art.soft.utils.Image.loadImage(hover);
            hover = null;
        }
    }

    @Override
    public void draw(art.soft.Graphics g){
        butBackground.instance.setMouseOver(mover);
        //im = console.isShow() ? im1 : im2;
        im = console ? im1 : im2;
        if (mover && im3!=null) {
            g.draw(im3, x, y, width, height);
        }
        super.draw(g);
        mover = false;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            //if (console.isShow()) console.hideWin();
            //else console.openWin();
            console ^= true;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (im1!=null) im1.dispose();
        if (im2!=null) im2.dispose();
        if (im3!=null) im3.dispose();
    }
}
