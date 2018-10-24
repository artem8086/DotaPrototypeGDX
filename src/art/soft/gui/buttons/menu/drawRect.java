package art.soft.gui.buttons.menu;

import art.soft.gui.buttons.IButton;

/**
 *
 * @author Artem
 */
public class drawRect extends IButton {

    public int color;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
    }

    @Override
    public void draw(art.soft.Graphics g){
        g.setStroke(2);
        g.setColor(color);
        g.drawRect(x, y, width, height);
        super.draw(g);
    }
}
