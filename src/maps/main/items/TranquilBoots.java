package maps.main.items;

import art.soft.spells.Effect;
import art.soft.units.ULevelCreep;
import art.soft.units.Unit;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class TranquilBoots extends Effect {

    @Override
    public Image getIcon() {
        return isNotCooldown() ? null : Icon;
    }

    @Override
    public int attackDamageAim(Unit u, Unit aim){
        startCooldown();
        return 0;
    }

    @Override
    public void doEffectsStats(Unit u, boolean stack) {
        if ((u.status & Unit.BREAK_SPELL)!=0) startCooldown();
        ULevelCreep uc = u.uLvl;
        if (isNotCooldown()) {
            uc.hpReg += 10;
            if (stack) Unit.add_speed += 90;
        } else
            if (stack) Unit.add_speed += 60;
        uc.t_am += 4;
    }
}
