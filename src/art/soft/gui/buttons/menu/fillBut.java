package art.soft.gui.buttons.menu;

import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;

/**
 *
 * @author Artem
 */
public class fillBut extends IButton {
    
    public boolean cancel = false;
    public int color;
    
    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
    }

    @Override
    public void draw(art.soft.Graphics g){
        if (color!=-1) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
        super.draw(g);
    }
    
    @Override
    public void pressed(int code, int k){
        if (code==Interface.BUTTON_PRESSED) {
            if (cancel) curWin.hideWin();
        }
    }
}
