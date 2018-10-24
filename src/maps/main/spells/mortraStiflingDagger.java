package maps.main.spells;

import art.soft.spells.Damage;
import art.soft.spells.Effect;
import art.soft.units.Attack;
import art.soft.units.ULevelHero;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class mortraStiflingDagger extends Effect {

    @Override
    public void doEffectsDamage(Unit u, boolean modif){
        if (level>0) {
            Unit.addPerSpeed -= 50;
        }
        isDelete = true;
    }

    @Override
    public void setLevel(int lvl){
        setCooldownTime((9 - lvl) * 1000);
        manacost = 15 + (4-lvl) * 5; 
        super.setLevel(lvl);
    }

    @Override
    public int startDamageAim(Unit u, Unit aim){
        if (owner==u && level>0) {
            Attack.mDmg = 40 + 20 * level;
            Attack.type = Damage.TYPE_CLEAR;
            if (aim.uLvl instanceof ULevelHero) Attack.mDmg >>= 1;
        }
        return 0;
    }

    @Override
    public void createModif(Unit u, Unit aim){
        setCooldownTime(level * 1000);
    }
}
