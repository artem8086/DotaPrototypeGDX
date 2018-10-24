package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;

/**
 *
 * @author Artem
 */
public class closeServ extends BOpenI {
    
    @Override
    public void pressed(int code, int k){
        super.pressed(code, k);
        if (code==Interface.BUTTON_PRESSED) Game.disconnect();
    }
}
