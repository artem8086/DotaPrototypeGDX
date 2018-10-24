package art.soft.units.actions;

import art.soft.specal.SpellBul;
import art.soft.spells.Effect;
import art.soft.spells.Spell;
import art.soft.units.Attack;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class CastSpellToPos extends moveToPos {

    private Spell spell;
    private int status;

    public void setSpell(Spell s, int x, int y) {
        this.x = x;
        this.y = y;
        spell = s;
        status = 0;
    }

    @Override
    public boolean act (Unit u) {
        if (status==0) {
            if (moveUnit(u, x, y) || spell.radiusS<0 || u.getDist(x, y) < spell.radiusS) {
                status = 1;
            }
        }
        Attack a = spell.getCastAttack();
        if (status==1) {
            if (a!=null) {
                u.setAnim(a.attack_anim);
                u.isCycle = false;
                status = 2;
            } else status = 3;
        }
        if (status==2) {
            if (a==null || u.getProgress()>=a.halfATime) {
                status = 3;
            }
            if (u.incAnm()) status = 3;
        }
        if (status==3) {
            if (a!=null && a.rangeBul!=null) {
                SpellBul.addSpellBul(u, a.rangeBul, x, y, spell, a.misSpeed, u.visGRP);
                status = 4;
            } else {
                Effect e = spell.getCastEffect();
                if (e!=null) {
                    e.castSpellToPos(x, y);
                    e.castSpell();
                }
                status = 5;
            }
        }
        if (status==4 && u.incAnm()) status = 5;
        return status==5;
    }

    public void relese() {
        spell = null;
    }
}
