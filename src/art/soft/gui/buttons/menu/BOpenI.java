package art.soft.gui.buttons.menu;

import art.soft.gui.Interface;
import static art.soft.gui.buttons.IButton.curWin;

/**
 *
 * @author Artem
 */
public class BOpenI extends PlayBut {

    public boolean hide = false;

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            if (!hide) curWin.hideWin();
            Interface w = Interface.getWindow(win);
            if (w!=null) {
                if (hide && w.isShow()) w.hideWin();
                else w.openWin();
            }
        }
    }
}
