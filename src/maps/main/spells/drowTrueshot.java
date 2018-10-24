package maps.main.spells;

import art.soft.spells.Effect;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class drowTrueshot extends Effect {

    @Override
    public void doEffectsDamage(Unit u, boolean modif){
        float s = 0.1f * level;
        u.uLvl.addTDamage((int) (s * u.uLvl.getMainDamage()));
        isDelete = true;
    }
}
