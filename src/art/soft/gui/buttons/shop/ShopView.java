package art.soft.gui.buttons.shop;

import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class ShopView extends IButton {

    static boolean shopView = true;

    private Image on, off;
    public String imgOn;
    public boolean status;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        off = im;
        if (imgOn!=null) {
            on = art.soft.utils.Image.loadImage(imgOn);
            imgOn = null;
        }
    }

    @Override
    public void draw(art.soft.Graphics g){
        im = status==shopView ? on : off;
        super.draw(g);
    }

    @Override
    public void pressed(int code, int k){
        if (code==Interface.BUTTON_PRESSED) {
            shopView = status;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (on!=null) on.dispose();
    }
    
}
