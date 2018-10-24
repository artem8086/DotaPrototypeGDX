package maps.main.items;

import art.soft.spells.Effect;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class SkadiEffect extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean modif){
        Unit.addPerSpeed -= 35;
        u.uLvl.add_AS -= 35;
        isDelete = true;
    }
}
