package art.soft.gui.buttons.shop;

import art.soft.Game;
import static art.soft.Game.CShost;
import art.soft.Graphics;
import art.soft.gui.Interface;
import art.soft.gui.buttons.DraggedBut;
import art.soft.gui.buttons.TButton;
import static art.soft.gui.buttons.inGame.Ability.drawCD;
import static art.soft.gui.buttons.inGame.ItemBut.dItem;
import static art.soft.gui.buttons.inGame.ItemBut.itemButH;
import static art.soft.gui.buttons.inGame.ItemBut.itemButW;
import art.soft.items.ShopItem;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class ShopBut extends TButton implements DraggedBut {

    private static final int COLOR_RED = 0xFF0000;

    public ShopItem item;

    public static ShopBut createBut(int x, int y, int w, int h, int fontSize, boolean bold, int c, ShopItem item) {
        ShopBut sb = new ShopBut();
        sb.x = x; sb.y = y;
        sb.width = w; sb.height = h;
        sb.color = c;
        sb.fontSize = fontSize;
        sb.bold = bold;
        sb.item = item;
        sb.langReload();
        return sb;
    }

    public void setToBut(ShopBut b) {
        b.x = x; b.y = y;
        b.active = active;
        if (item!=null) {
            b.im = item.uData.Icon;
            if (b.im==null) b.im = dItem;
        }
        b.width = width;
        b.height = height;
        b.bold = bold;
        b.fontSize = fontSize;
        b.font = font;
        b.item = item;
    }

    @Override
    public void draw(Graphics g) {
        im = null;
        if (item!=null) {
            active = true;
            Image i = item.uData.Icon;
            if (i==null) i = dItem;
            int cd = item.getCooldown(Game.yourPlayer);
            g.draw(i, x, y, width, height);
            if (cd>0) {
                drawCD(g, this, cd, item.cooldown);
                int c = cd / 1000;
                if (c>60) {
                    int sec = c % 60;
                    setText("" + (c/60) + (sec>9 ? ":" : ":0") + sec);
                } else if (c!=0) setText("" + c);
                else setText("0." + (cd / 100));
                active = false;
            } else
            if (item.priceItem<=Game.yourPlayer.getGold()) {
                int wh = width >> 2;
                int hh = height >> 2;
                if ((Game.yourPlayer.shopsMask & (1 << item.curShopID))==0)
                    g.setAlpha(COLOR_RED);
                g.draw(ShopOpen.availableT, x-wh, y-hh, width+wh+wh, height+hh+hh);
                g.setAlphaNormal();
            }
            super.draw(g);
            im = i;
        } else
            active = false;
    }

    @Override
    protected void keyPressed(int key) {
        pressed(Interface.BUTTON_RELESSED, key);
    }

    @Override
    protected void keyRelessed(int key) {}

    @Override
    public void pressed(int code, int k){
        if (code==Interface.BUTTON_RELESSED) {
            ItemDetail.instance.setItem(item);
        } else
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
