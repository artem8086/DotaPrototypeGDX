package maps.main.spells;

import art.soft.spells.Effect;
import art.soft.units.ULevelHero;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class drowFrostArrows extends Effect {

    @Override
    public void doEffectsDamage(Unit u, boolean modif){
        if (level>0) {
            Unit.addPerSpeed -= 20 * level;
        }
        isDelete = true;
    }

    @Override
    public int startDamageAim(Unit u, Unit aim){
        if (owner==u) return modif;
        return 0;
    }

    @Override
    public int attackDamageAim(Unit u, Unit aim){
        if (owner==u) return modif;
        return 0;
    }

    @Override
    public void createModif(Unit u, Unit aim){
        if (aim!=null) {
            if (aim.uLvl instanceof ULevelHero) setCooldownTime(1500);
            else setCooldownTime(3000);
        }
    }
}
