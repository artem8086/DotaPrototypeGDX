package art.soft.gui.buttons.shop;

import art.soft.gui.buttons.IButton;

/**
 *
 * @author Artem
 */
public class Shops extends IButton {

    static int shopID;
    static Shops instance;

    int shopGroup, groupID;
    public int ID;
    public boolean main = false;

    @Override
    public void load(double xk, double yk) {
        shopID = ID;
        super.load(xk, yk);
    }

    @Override
    public void draw( art.soft.Graphics g){
        if (shopID==ID) {
            active = true;
            if (main) instance = this;
            super.draw(g);
        } else
            active = false;
    }
}
