package art.soft.gui.buttons.settings;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.Settings;

/**
 *
 * @author Artem
 */
public class RenderSet extends CheckBut {

    public int keyH;

    @Override
    public void draw(art.soft.Graphics g){
        flag = Game.settings.getGraphicsKey(keyH, flag);
        super.draw(g);
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            flag ^= true;
            Game.settings.setGraphicsKey(keyH, flag);
            Settings.Save(Game.settings);
            Game.settings.applyRender();
        }
    }
}
