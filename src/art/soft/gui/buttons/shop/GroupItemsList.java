package art.soft.gui.buttons.shop;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.gui.buttons.IButton;
import art.soft.gui.buttons.TButton;
import static art.soft.gui.buttons.inGame.ItemBut.dItem;
import static art.soft.gui.buttons.shop.ShopView.shopView;
import static art.soft.gui.buttons.shop.Shops.shopID;
import art.soft.items.Shop;
import com.badlogic.gdx.graphics.Color;
import art.soft.utils.Image;
import com.badlogic.gdx.utils.Align;

/**
 *
 * @author Artem
 */
public class GroupItemsList extends TButton {

    static GroupItemsList instance;
    static Image itemBG;
    public int num, imW, imH, imX, imY, textX, textW;

    @Override
    public void load(double xk, double yk) {
        super.load(xk, yk);
        if (im!=null) itemBG = im;
        im = null;
        imW *= xk; imH *= yk;
        imX *= xk; imX *= yk;
        textX *= xk; textW *= xk;
        Init();
    }

    private void Init() {
        int butH = height;
        int h = 0;
        buttons = new IButton[num];
        for (int i=0; i<num; i++) {
            buttons[i] = ShopButList.createBut(x, y + h, width, butH);
            h += butH;
        }
        height = h;
    }

    @Override
    public void draw(Graphics g) {
        if (!shopView) {
            instance = this;
            if (buttons!=null) {
                Shop s = Shop.getShop(shopID);
                if (s!=null) {
                    int item = s.startItem[Shops.instance.shopGroup];
                    int num = s.numItems[Shops.instance.shopGroup];
                    if (num>buttons.length) num = buttons.length;
                    int i=0;
                    for (; i<num; i ++)
                        ((ShopButList) buttons[i]).item = s.items[item ++];
                    for (; i<buttons.length; i ++)
                        ((ShopButList) buttons[i]).item = null;
                }
            }
            active = true;
            super.draw(g);
        } else
            active = false;
    }

    public Image drawButton(Graphics g, ShopButList b) {
        if (b.item!=null) {
            int x = b.x; int y = b.y;
            int w = b.width;
            int h = b.height;
            g.draw(itemBG, x, y, w, h);
            Image i = b.item.uData.Icon;
            if (i==null) i = dItem;
            g.draw(i, x+imX, y+imY, imW, imH);
            g.setFont(font);
            g.setColor(color);
            int fh = (g.fontAscent() - (g.fontHeight()>>1)) + (h >> 1);
            g.drawString(b.item.uData.name1, x+textX, y + fh);
            int pr = b.item.priceItem;
            g.setColor(pr<=Game.yourPlayer.getGold() ? Color.YELLOW : Color.WHITE);
            g.drawString(Integer.toString(pr), x+textX, y + fh, textW, Align.right);
            return i;
        }
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (itemBG!=null) itemBG.dispose();
    }
}
