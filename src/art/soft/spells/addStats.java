package art.soft.spells;

import art.soft.units.ULevelHero;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class addStats extends Effect {

    public float statsInc = 2;
    
    @Override
    public Effect copy(Effect e){
        addStats as = (addStats) super.copy(e);
        as.statsInc = statsInc;
        return as;
    }
    
    @Override
    public void doEffectsStats(Unit u, boolean modif){
        float s = statsInc * level;
        ULevelHero ul = (ULevelHero) u.uLvl;
        ul.t_str += s;
        ul.t_agl += s;
        ul.t_int += s;
    }
}
