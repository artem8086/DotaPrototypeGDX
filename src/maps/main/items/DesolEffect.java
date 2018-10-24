package maps.main.items;

import art.soft.spells.Effect;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class DesolEffect extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean modif){
        u.uLvl.t_am -= 7;
        isDelete = true;
    }
}
