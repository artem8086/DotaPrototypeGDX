package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;

/**
 *
 * @author Artem
 */
public class startMap extends PlayBut {

    private boolean pressed;

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            pressed = true;
            Interface.butClear = false;
        } else if (pressed) {
            pressed = false;
            Game.disconnect();
            Game.mapInfo = mapsPool.customMap;
            Interface.getWindow(win).openWin();
        }
    }
}
