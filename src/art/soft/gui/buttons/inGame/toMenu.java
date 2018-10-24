package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class toMenu extends IButton {
    
    public String back;
    public Image im1, im2;
    public boolean mover = false;
    
    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        im1 = im;
        if (back!=null) {
            im2 = art.soft.utils.Image.loadImage(back);
            back = null;
        }
    }

    @Override
    public void draw(art.soft.Graphics g){
        im = mover ? im2 : im1;
        super.draw(g);
        mover = false;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            Game.HUD = Game.Menu;
        }
    }

    @Override
    public void dispose(){
        super.dispose();
        if (im2!=null) im2.dispose();
    }
}
