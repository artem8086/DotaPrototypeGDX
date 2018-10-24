package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;
import static art.soft.gui.buttons.IButton.curWin;

/**
 *
 * @author Artem
 */
public class disconnect extends PlayBut {

    @Override
    public void draw(art.soft.Graphics g){
        Interface w = curWin.prev.windows;
        if (Game.initGame) {
            super.draw(g);
            active = true;
        } else
            active = false;
    }
    
    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            Game.disconnect();
        }
    }
}
