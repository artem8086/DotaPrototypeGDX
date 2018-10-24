package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.gui.buttons.TButton;
import art.soft.multiplayer.Player;
import art.soft.units.Unit;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class goldRes extends TButton {

    public int wgr;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        wgr *= xk;
    }

    @Override
    public void draw(Graphics g) {
        Unit u = Game.yourPlayer.gUnit.selUnit;
        if (u!=null && u.owners!=null && u.owners.youInGroup()) {
            Player p = u.getOwnerPlayer();
            if (p!=null) {
                Image tim = im;
                int wd = width;
                width = wgr;
                text = null;
                super.draw(g);
                im = null;
                width = wd;
                text = Integer.toString(p.getGold());
                super.draw(g);
                im = tim;
            }
        }
    }
}
