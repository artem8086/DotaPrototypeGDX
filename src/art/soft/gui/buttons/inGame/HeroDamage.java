package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.buttons.TButton;
import art.soft.units.Attack;
import art.soft.units.Unit;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Артем
 */
public class HeroDamage extends TButton{
    
    private int m_dmg, t_dmg;
    
    public void draw(art.soft.Graphics g){
        active = false;
        Unit u = Game.yourPlayer.gUnit.selUnit;
        if (u!=null && u.uLvl!=null && u.uLvl.attacks!=null) {
            Attack a = u.uLvl.attacks[0];
            if (a!=null) {
                active = true;
                super.draw(g);
                g.setColor(color);
                g.setFont(font);
                if (Game.yourPlayer.owner.groupInGroup(u.isVisible)) {
                    m_dmg = a.m_dmg;
                    t_dmg = a.t_dmg;
                }
                String s = ""+m_dmg;
                int w1 = g.stringWidth(s) >> 1;
                int h = (g.fontAscent() - (g.fontHeight()>>1));
                int da = t_dmg;
                String s2 = null;
                int w2 = 0;
                if (da!=0) {
                    if (da>0) s2 = "+" + da;
                    else s2 = "" + da;
                    w2 = g.stringWidth(s) >> 1;
                }
                g.drawString(s, x - w1 - w2 + (width>>1), y + h + (height>>1));
                if (da!=0) {
                    if (da>0) g.setColor(Color.GREEN);
                    else g.setColor(Color.RED);
                    g.drawString(s2, x - w1 + w2 + (width>>1), y + h + (height>>1));
                }
            }
        }
    }
}
