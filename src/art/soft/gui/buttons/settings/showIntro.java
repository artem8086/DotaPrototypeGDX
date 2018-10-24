package art.soft.gui.buttons.settings;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.Settings;

/**
 *
 * @author Artem
 */
public class showIntro extends CheckBut {

    @Override
    public void draw(art.soft.Graphics g){
        flag = Game.settings.showIntro;
        super.draw(g);
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            flag ^= true;
            Game.settings.showIntro = flag;
            Settings.Save(Game.settings);
        }
    }
}
