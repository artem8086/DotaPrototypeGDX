package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;

/**
 *
 * @author Artem
 */
public class fastRun extends BOpenW {
    
    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            //Interface w = Game.HUD;
            //for (int r : win)
            //    w = w.getWindow(r);
            //w.show = false;
            super.pressed(code, k);
            Game.connectOffline();
            //Game.reset = Game.RESET_START_GAME;
        }
    }
}
