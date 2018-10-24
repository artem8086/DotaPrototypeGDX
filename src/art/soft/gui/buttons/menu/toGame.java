package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;

/**
 *
 * @author Artem
 */
public class toGame extends IButton {

    public boolean mover = false;
    public int color2, color1;

    @Override
    public void draw(art.soft.Graphics g){
        if (Game.initGame) {
            active = true;
            g.setAlpha(mover ? color1 : color2);
            super.draw(g);
            g.setAlphaNormal();
            mover = false;
        } else active = false;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            Game.HUD = Game.inGame;
        }
    }
}
