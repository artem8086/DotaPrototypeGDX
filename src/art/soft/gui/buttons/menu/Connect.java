package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;

/**
 *
 * @author Artem
 */
public class Connect extends BOpenI {
    
    public boolean isServer;
    
    @Override
    public void pressed(int code, int k){
        mover = true;
        super.pressed(code, k);
        if (code==Interface.BUTTON_PRESSED) Game.connect(isServer);
    }
}
