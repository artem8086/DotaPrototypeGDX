package art.soft.gui.buttons.settings;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.Settings;

/**
 *
 * @author Artem
 */
public class showFPS extends CheckBut {

    @Override
    public void draw(art.soft.Graphics g){
        flag = Game.settings.showFPS;
        super.draw(g);
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            flag ^= true;
            Game.settings.showFPS = flag;
            Settings.Save(Game.settings);
        }
    }
}
