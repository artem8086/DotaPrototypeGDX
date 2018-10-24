package art.soft.gui.buttons.settings;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.Loading;
import art.soft.gui.Settings;

/**
 *
 * @author Artem
 */
public class Reset extends SetBut {

    @Override
    public void pressed(int code, int k){
        super.pressed(code, k);
        if (code==Interface.BUTTON_PRESSED) {
            Game.reset = Game.RESET_SETTINGS;
            
        }
    }
}
