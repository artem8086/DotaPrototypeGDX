package maps.main.items;

import art.soft.Game;
import art.soft.spells.Effect;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class RadianceEffect extends Effect {

    @Override
    public void doEffectsDamage(Unit u, boolean stack) {
        if (Game.secondTick) {
            //u.status |= Unit.BREAK_SPELL;
            u.dealsMagicDamage(50, owner);
        }
        isDelete = true;
    }
}
