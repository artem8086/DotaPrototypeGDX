package maps.main.spells;

import art.soft.spells.Effect;
import art.soft.units.ULevelHero;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class drowMarksmanship extends Effect{
    
    @Override
    public void doEffectsStats(Unit u, boolean modif){
        if (level>0) {
            ((ULevelHero) (u.uLvl)).m_agl += 200 + level * 200;
        }
        isDelete = true;
    }
}
