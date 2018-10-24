package art.soft.gui.buttons.shop;

import static art.soft.Game.CShost;
import art.soft.Graphics;
import art.soft.gui.Interface;
import art.soft.gui.buttons.DraggedBut;
import art.soft.gui.buttons.IButton;
import art.soft.items.ShopItem;
import art.soft.utils.Image;
import static art.soft.gui.buttons.inGame.ItemBut.itemButH;
import static art.soft.gui.buttons.inGame.ItemBut.itemButW;

/**
 *
 * @author Artem
 */
public class ShopButList extends IButton implements DraggedBut {

    public ShopItem item;
    private boolean mover;

    public static ShopButList createBut(int x, int y, int w, int h) {
        ShopButList sb = new ShopButList();
        sb.x = x; sb.y = y;
        sb.width = w; sb.height = h;
        return sb;
    }

    @Override
    public void draw(Graphics g) {
        im = null;
        if (item!=null) {
            active = true;
            Image i = GroupItemsList.instance.drawButton(g, this);
            im = null;
            super.draw(g);
            im = i;
        } else
            active = false;
        mover = false;
    }

    @Override
    protected void keyPressed(int key) {
        pressed(Interface.BUTTON_RELESSED, key);
    }

    @Override
    protected void keyRelessed(int key) {}

    @Override
    public void pressed(int code, int k){
        mover = true;
        //if (code==Interface.MOUSE_OVER) {
            
        //} else
        if (code==Interface.ALTERNATIVE_PRESSED) {
            if (item!=null) CShost.playerBuyItem(item.shopID, item.ID);
        }
    }

    @Override
    public void DraggedDraw(Graphics g, int x, int y) {
        if (im!=null) {
            g.draw(im, x - (itemButW>>1), y - (itemButH>>1), itemButW, itemButH);
	}
    }
}
