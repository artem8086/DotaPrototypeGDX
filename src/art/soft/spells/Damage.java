package art.soft.spells;

import static art.soft.units.Attack.setModificators;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class Damage extends Effect {

    public static final int TYPE_NONE = 0;
    public static final int TYPE_PHYTH = 1;
    public static final int TYPE_MAGIC = 2;
    public static final int TYPE_CLEAR = 3;
    public static final int TYPE_MISS  = 4;
    public static final int DONT_BREAK_SPELL = 8;

    public int dmg;
    
    public Effect effects;

    public Damage(){
        attrib = 0;
    }

    @Override
    public Effect copy(Effect e){
        Damage d = (Damage) super.copy(e);
        d.effects = effects;
        d.dmg = dmg;
        return d;
    }

    public void setDamege(Unit aim) {
        /*Effect e = next;
        if (e==null) e = this;
        else {
            Effect n = e;
            while (n.next!=null) n = n.next;
            n.next = this;
            next = null;
        }
        setModificators(e, aim);*/
        setModificators(this, aim);
    }

    /*@Override
    public void doAttackPos(int x, int y){
        Effect e = effects;
        Effect n;
        while (e!=null) {
            n = e.next;
            if (!e.isDelete) e.doAttackPos(x, y);
            Effect.poolEffect(e);
            e = n;
        }
        Attack.poolsModificators(next);
        effects = null;
        isDelete = true;
    }*/

    @Override
    public void doEffectsDamage(Unit u, boolean modif){
        Unit.damage = 0;
        level &= 7;
        if (level==TYPE_PHYTH) u.dealsPhythDamage(dmg, owner); else
        if (level==TYPE_MAGIC) u.dealsMagicDamage(dmg, owner); else
        if (level==TYPE_CLEAR) u.dealsClearDamage(dmg, owner);
        int damage = Unit.damage;
        Effect e = effects;
        Effect n;
        while (e!=null) {
            n = e.next;
            if (!e.isDelete) e.doAttackModificator(u, damage, dmg);
            Effect.poolEffect(e);
            e = n;
        }
        //Attack.poolsModificators(effects);
        effects = null;
        isDelete = true;
    }
}
