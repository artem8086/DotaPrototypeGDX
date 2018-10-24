package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class pageButton extends IButton {

    public int pageNum;
    public String im1, im2;
    private static Image img1, img2;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        if (im1!=null) {
            img1 = art.soft.utils.Image.loadImage(im1);
            im1 = null;
        }
        if (im2!=null) {
            img2 = art.soft.utils.Image.loadImage(im2);
            im2 = null;
        }
    }

    @Override
    public void draw(art.soft.Graphics g){
        if (active) {
            im = pageNum==GroupUnit.page ? img1 : img2;
            super.draw(g);
        }
    }

    @Override
    public void pressed(int code, int k){
        if (code==Interface.BUTTON_PRESSED) {
            GroupUnit.page = pageNum;
        }
        if (code!=Interface.MOUSE_OVER) Game.statusKey = Game.DO_NOTHING;
    }

    @Override
    public void dispose(){
        super.dispose();
        if (img1!=null) { img1.dispose(); img1 = null; }
        if (img2!=null) { img2.dispose(); img2 = null; }
    }
}
