package maps.main.items;

import art.soft.spells.Effect;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class SangeEffect extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean modif){
        Unit.addPerSpeed -= 20;
        u.uLvl.add_AS -= 20;
        isDelete = true;
    }
}
