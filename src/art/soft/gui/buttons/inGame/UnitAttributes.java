package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.buttons.IButton;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class UnitAttributes extends IButton {

    public static Image attribIM;

    @Override
    public void load(double xk, double yk){
        if (Image!=null) {
            attribIM = im = art.soft.utils.Image.loadImage(Image);
            Image = null;
        }
        super.load(xk, yk);
    }

    @Override
    public void draw(art.soft.Graphics g){
        if (active = (Game.yourPlayer.gUnit.next==null)) {
            super.draw(g);
        }
    }
}
