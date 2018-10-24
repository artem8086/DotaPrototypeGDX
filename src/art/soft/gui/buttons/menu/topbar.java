package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.buttons.IButton;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class topbar extends IButton {
    
    public static topbar instance;
    public Image topbar, imt;
    public float opacity, startOp, speedOp;
    
    @Override
    public void load(double xk, double yk){
        instance = this;
        super.load(xk, yk);
        opacity = 1;
    }

    @Override
    public void draw( art.soft.Graphics g){
        if (opacity<1) {
            g.draw(topbar, x, y, width, height);
            g.setAlpha(opacity);
            g.draw(imt, x, y, width, height);
            g.setAlphaNormal();
            opacity += speedOp * Game.deltaT;
        } else {
            g.draw(imt, x, y, width, height);
            opacity = 1;
        }
        super.draw(g);
        //img = topbar;
    }

    public void setImage(Image i){
        if (i!=null && i!=imt) {
            topbar = imt;
            imt = i;
            opacity = startOp;
        }
    }
}
