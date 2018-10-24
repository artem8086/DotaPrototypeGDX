package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class quit extends IButton {

    public boolean mover = false;
    public int color2, color1;

    @Override
    public void draw(art.soft.Graphics g){
        if (mover) {
            if (im!=null) g.draw(im, x, y, width, height);
            g.setAlpha(color1);
        } else
            g.setAlpha(color2);
        Image i = im;
        im = null;
        super.draw(g);
        g.setAlphaNormal();
        mover = false;
        im = i;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            Game.gameExit();
        }
    }
}
