package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.buttons.TButton;
import art.soft.units.Unit;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Артем
 */
public class HeroArmor extends TButton{
    
    private int s_am, t_am;
    
    @Override
    public void draw(art.soft.Graphics g){
        active = false;
        Unit u = Game.yourPlayer.gUnit.selUnit;
        if (u!=null && u.uLvl!=null) {
            active = true;
            super.draw(g);
            g.setColor(color);
            g.setFont(font);
            if (Game.yourPlayer.owner.groupInGroup(u.isVisible)) {
                s_am = (int) u.uLvl.s_am;
                t_am = (int) u.uLvl.t_am;
            }
            String s = "" + s_am;
            int w = g.stringWidth(s) >> 1;
            int a = g.fontAscent();
            g.drawString(s, x - w + (width>>1), y + (a >> 1) + height);
            if (t_am!=0) {
                if (t_am>0) {
                    g.setColor(Color.GREEN);
                    s = "+" + t_am;
                } else {
                    g.setColor(Color.RED);
                    s = "" + t_am;
                }
                int h = a + (a>>1) + g.fontHeight();
                w = g.stringWidth(s) >> 1;
                g.drawString(s, x - w + (width>>1), y + h + height);
            }
        }
    }
}
