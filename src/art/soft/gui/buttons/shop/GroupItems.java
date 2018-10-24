package art.soft.gui.buttons.shop;

import art.soft.Graphics;
import art.soft.gui.buttons.IButton;
import art.soft.gui.buttons.TButton;
import static art.soft.gui.buttons.shop.ShopView.shopView;
import static art.soft.gui.buttons.shop.Shops.shopID;
import art.soft.items.Shop;

/**
 *
 * @author Artem
 */
public class GroupItems extends TButton {

    public int group, ID, deltaH, deltaW, column;
    //private boolean init = false;

    @Override
    public void load(double xk, double yk) {
        super.load(xk, yk);
        deltaW *= xk;
        deltaH *= yk;
        Init();
    }

    private void Init() {
        Shop s = Shop.getShop(shopID);
        if (s!=null) {
            int butH = height;
            int w = 0;
            int c = column;
            int num = 0;
            for (int j = group; c>0; j ++) {
                num += s.numItems[j];
                c --;
            }
            int butN = 0;
            buttons = new IButton[num];
            c = column;
            for (int j = group; c>0; j ++) {
                int h = 0;
                int item = s.startItem[j];
                num = s.numItems[j];
                for (int i=0; i<num; i++) {
                    buttons[butN ++] = ShopBut.createBut(x + w, y + h, width, butH, fontSize, bold, color, s.items[item++]);
                    h += butH + deltaH;
                }
                if (h>height) height = h;
                w += width + deltaW;
                c --;
            }
            width = w;
        }
        //init = true;
    }

    @Override
    public void draw(Graphics g) {
        active = false;
        if (shopView) {
            //if (!init) Init();
            if (Shops.instance.groupID==ID) {
                active = true;
                super.draw(g);
            }
        }
    }
}
