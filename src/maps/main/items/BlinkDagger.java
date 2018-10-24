package maps.main.items;

import art.soft.spells.Effect;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class BlinkDagger extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean stack) {
        if ((u.status & Unit.BREAK_SPELL)!=0) {
            startCooldown(3000);
        }
    }

    @Override
    public void useItemToPos(Unit owner, Unit item, int slot, int x, int y) {
        owner.teleport(x, y);
    }
}
