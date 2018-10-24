package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.buttons.TButton;
import art.soft.spells.Spell;
import art.soft.units.Unit;
import static art.soft.units.Unit.IS_SILENCE;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class AbilityPool extends TButton {

    public String emptyAbility;
    public static Image eAbility;
    public int xd4, xd5;
    public static int dx, xb, cyct;
    
    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        xd4 *= xk; xd5 *= xk;
        if (emptyAbility!=null) {
            eAbility = art.soft.utils.Image.loadImage(emptyAbility);
            emptyAbility = null;
        }
    }

    @Override
    public void draw(art.soft.Graphics g){
        Unit u = Game.yourPlayer.gUnit.selUnit;
        Spell[] spells = null;
        int ss = 0; int ns = 0;
        boolean su = true;
        if (u!=null && u.uLvl!=null) {
            if (Game.yourPlayer.owner.groupInGroup(u.canControl)) {
                spells = u.spells;
                ss = u.startS;
                ns = u.numS;
            } else {
                spells = u.uData.spells;
                ss = u.uData.startS;
                ns = u.uData.numS;
                su = false;
            }
            if (ns>6) ns = 6;
        }
        if (ns<=4) dx = xd4; else
        if (ns==5) dx = xd5; else
        dx = 0;
        xb = 0;
        int i = 0;
        for (i=5; i>=0; i --) ((Ability) buttons[i]).spell = null;
        if (spells!=null) {
            boolean canCast = (u.status & IS_SILENCE)==0;
            for (i=0; i<ns; i ++) {
                Ability a = (Ability) buttons[i];
                a.spell = spells[ss];
                if (a.spell==null) { ns --;  continue; }
                ss ++;
                a.sNum = i;
                a.canCast = canCast;
                if (su) a.sUp = a.spell.canUpgradeSpell(u);
                else a.sUp = 0;
            }
        } else ns = 0;
        super.draw(g);
        xb = 0;
        cyct += Game.tFrame;
        if (cyct>=1024) cyct = 0;
    }

    @Override
    public void dispose(){
        super.dispose();
        if (eAbility!=null) { eAbility.dispose(); eAbility = null; }
    }
}
