package art.soft.gui.buttons.shop;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.buttons.menu.BOpenI;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class ShopOpen extends BOpenI {

    static Image availableT;

    public String  available;

    @Override
    public void load(double xk, double yk) {
        super.load(xk, yk);
        if (available!=null) {
            availableT = art.soft.utils.Image.loadImage(available);
            available = null;
        }
    }

    @Override
    public void pressed(int code, int k){
        super.pressed(code, k);
        if (code==Interface.BUTTON_PRESSED) {
            Shops.shopID = Game.yourPlayer.curShop;
            //ShopGroup.shopGroup = ShopGroup.groupID = 0;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (availableT!=null) { availableT.dispose(); availableT = null; }
    }
}
