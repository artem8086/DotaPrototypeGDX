package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;

/**
 *
 * @author Artem
 */
public class Play extends PlayBut {

    public boolean connect = false;

    @Override
    public void draw(art.soft.Graphics g){
        if (!curWin.prev.windows.show && !Game.initGame &&
            (connect==(Game.mapInfo==null ? false : Game.mapInfo.connect))) {
            super.draw(g);
            active = true;
        } else
            active = false;
    }
    
    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            Interface w = curWin.prev.windows;
            if (!w.show) { w.openWin(); }
            if (!connect) Game.mapInfo = Game.mainMap;
        }
    }
}
