package art.soft.gui.buttons.settings;

import art.soft.Game;
import art.soft.gui.buttons.IButton;
import art.soft.gui.buttons.HScroll;

/**
 *
 * @author Artem
 */
public class LangPool extends HScroll {

    public int butH;

    @Override
    public void load(double xk, double yk){
        int ln = Game.language.locales.length;
        //poolH = butH * ln;
        buttons = new IButton[ln];
        for (int i=0; i<ln; i++) {
            buttons[i] = LangBut.createBut(x, 0, width, butH, fontSize, bold, color, Game.language.locales[i], i);
        }
        super.load(xk, yk);
    }
}
