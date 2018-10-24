package art.soft.gui.buttons.settings;

import art.soft.Game;
import art.soft.gui.buttons.TButton;

/**
 *
 * @author Artem
 */
public class Resolution extends TButton {

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        text = "" + Game.w + "x" + Game.h;
    }
}
