package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.buttons.TButton;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class HeroSpeed extends TButton {

    @Override
    public void draw(art.soft.Graphics g){
        Unit u = Game.yourPlayer.gUnit.selUnit;
        if (u!=null) {
            super.draw(g);
            g.setColor(color);
            g.setFont(font);
            int ms = 0;
            if (Game.yourPlayer.owner.groupInGroup(u.isVisible)) {
                ms = u.getSpeed();
            }
            String s = "" + ms;
            int w = g.stringWidth(s) >> 1;
            g.drawString(s, x - w + (width>>1), y + (g.fontAscent() >> 1) + height);
        }
    }
}
