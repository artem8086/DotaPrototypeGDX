package maps.main.items;

import art.soft.spells.Effect;
import art.soft.units.ULevelCreep;
import art.soft.units.ULevelHero;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class Tarrasque extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean stack) {
        if ((u.status & Unit.BREAK_SPELL)!=0) {
            setCooldownTime(u.uLvl.getAttackType()==1 ? 4000 : 6000);
            startCooldown();
        }
        ULevelCreep uc = u.uLvl;
        if (isNotCooldown()) {
            Unit.addPerHPReg += 0.02;
        }
        uc.hp += 250;
        if (uc instanceof ULevelHero) {
           ((ULevelHero) uc).t_str += 40;
        }
    }
}
