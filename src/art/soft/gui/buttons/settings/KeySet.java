package art.soft.gui.buttons.settings;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.Settings;
import art.soft.gui.buttons.TButton;
import com.badlogic.gdx.Input.Keys;

/**
 *
 * @author Artem
 */
public class KeySet extends TButton {

    public boolean mover = false;
    public int keyNum;
    private static final int col1 = 0x404040;
    private static final int col2 = 0x202020;
    private static final int col3 = 0x909090;
    private static final int col4 = 0x1111CC;

    @Override
    public void draw(art.soft.Graphics g){
        if (curWin.activeWin!=keyNum) {
            g.setColor(mover ? col1 : col2);
            int k = Game.settings.keyCodes[keyNum];
            setText(k==0 ? null : Keys.toString(k));
            key = 0;
        } else {
            g.setColor(col4);
            text = null;
        }
        g.fillRect(x, y, width, height);
        g.setColor(col3);
        g.setStroke(2);
        g.drawRect(x, y, width, height);
        super.draw(g);
        mover = false;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            if (key<0) {
                if (k>0) {
                    int keys[] = Game.settings.keyCodes;
                    for (int i = keys.length-1; i>=0; i--) {
                        if (keys[i]==k) keys[i] = 0;
                    }
                    keys[keyNum] = k;
                    Settings.Save(Game.settings);
                }
                key = 0;
                curWin.activeWin = -1;
            } else {
                curWin.activeWin = keyNum;
                key = -1;
            }
        }
    }
}
