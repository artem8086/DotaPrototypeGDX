package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.buttons.TButton;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class HeroName extends TButton {
    
    @Override
    public void draw(art.soft.Graphics g){
        Unit u = Game.yourPlayer.gUnit.selUnit;
        if (u!=null) {
            setText(u.uData.name1);
        }
        super.draw(g);
    }
}
