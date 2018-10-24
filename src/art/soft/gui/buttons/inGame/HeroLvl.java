package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.buttons.TButton;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class HeroLvl extends TButton {
    
    private int lvl;
    
    @Override
    public void draw(art.soft.Graphics g){
        Unit u = Game.yourPlayer.gUnit.selUnit;
        if (u!=null && u.uLvl!=null) {
            if (Game.yourPlayer.owner.groupInGroup(u.isVisible)) lvl = u.uLvl.level;
            setText(""+lvl);
        }
        super.draw(g);
    }
}
