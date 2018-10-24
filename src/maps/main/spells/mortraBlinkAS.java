package maps.main.spells;

import art.soft.spells.Effect;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class mortraBlinkAS extends Effect {

    public Unit aim;

    @Override
    public void doEffectsStats(Unit u, boolean modif){
        u.uLvl.add_AS += 80 + 80 * level;
        isDelete = true;
    }

    @Override
    public int startDamageAim(Unit u, Unit aim){
        if (aim!=this.aim) resetCooldown();
        return 0;
    }
}
