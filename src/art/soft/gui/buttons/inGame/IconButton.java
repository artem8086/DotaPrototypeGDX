
package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.spells.Spell;
import art.soft.units.Unit;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class IconButton extends IButton {
    
    static Image dPortrait;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        dPortrait = im;
    }
    
    @Override
    public void draw(art.soft.Graphics g){
        im = dPortrait;
        Unit u = Game.yourPlayer.gUnit.selUnit;
        if (u!=null) {
            Image i = u.uData.portraitIm;
            if (i!=null) im = i;
            g.draw(im, x, y, width, height);
	}
    }

    @Override
    public void dispose() {
        if (dPortrait!=null) { dPortrait.dispose(); dPortrait = null; }
    }
}
