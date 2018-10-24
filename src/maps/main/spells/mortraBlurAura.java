package maps.main.spells;

import art.soft.Game;
import art.soft.spells.Effect;
import art.soft.units.Unit;
import static art.soft.units.Unit.MINI_MAP_VIS;

/**
 *
 * @author Артем
 */
public class mortraBlurAura extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean modif){
        isDelete = true;
    }
    
    @Override
    public void doEffectsDamage(Unit u, boolean stack) {
        if (level>0) {
            if (!Game.yourPlayer.owner.groupInGroup(u.frendGRP))
                u.status &= ~MINI_MAP_VIS;
        }
    }
}
