package art.soft.gui.buttons.shop;

import art.soft.Graphics;
import art.soft.gui.Interface;
import art.soft.gui.buttons.TButton;
import static art.soft.gui.buttons.shop.ShopView.shopView;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class ShopGroup extends TButton {

    Image on, off;
    private static Image back;
    public String imgPrs, backIm;
    int group, ID;
    boolean mover;

    @Override
    public void load(double xk, double yk) {
        super.load(xk, yk);
        on = im;
        if (imgPrs!=null) {
            off = art.soft.utils.Image.loadImage(imgPrs);
            imgPrs = null;
        }
        if (backIm!=null) {
            back = art.soft.utils.Image.loadImage(backIm);
            backIm = null;
        }
    }

    @Override
    public void draw(Graphics g) {
        int tx = x; int ty = y;
        int twidth = width;
        int theight = height;
        if (Shops.instance.groupID==ID) {
            if (!shopView) {
                if (group==Shops.instance.shopGroup) {
                    g.draw(back, x, y, width, height);
                    int wh = twidth >> 4;
                    int hh = theight >> 4;
                    x += wh; y += hh;
                    width -= wh + wh;
                    height -= hh + hh;
                }
                if (mover) g.setAlpha(0xAAAAAA);
            }
            active = true;
            im = on;
        } else {
            active = off!=null;
            im = off;
        }
        super.draw(g);
        g.setAlphaNormal();
        x = tx; y = ty;
        width = twidth;
        height = theight;
        mover = false;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            if (group>=0) Shops.instance.shopGroup = group;
            Shops.instance.groupID = ID;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (back!=null) { back.dispose(); back = null; }
        if (off!=null) off.dispose();
    }
}
