package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class background extends fillBut {

    @Override
    public void draw(art.soft.Graphics g){
        if (Game.initGame) {
            Image i = im;
            im = null;
            super.draw(g);
            im = i;
        }
        else if (im!=null)
            g.draw(im, x, y, width, height);
    }
}
