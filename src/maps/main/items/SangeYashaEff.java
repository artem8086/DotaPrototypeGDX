package maps.main.items;

import art.soft.spells.Effect;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class SangeYashaEff extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean modif){
        Unit.addPerSpeed -= 30;
        u.uLvl.add_AS -= 30;
        isDelete = true;
    }
}
