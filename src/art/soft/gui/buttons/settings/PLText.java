package art.soft.gui.buttons.settings;

import art.soft.gui.buttons.TButton;
import com.badlogic.gdx.utils.Align;

/**
 *
 * @author Artem
 */
public class PLText extends TButton {

    public int numBut = -1;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        mode = Align.left;
    }

    @Override
    public void pressed(int code, int k){
        if (numBut>=0) {
            curWin.buttons[numBut].pressed(code, k);
        }
    }
}
