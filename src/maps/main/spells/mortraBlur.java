package maps.main.spells;

import art.soft.spells.Effect;
import static art.soft.multiplayer.ClientServer.random;
import static art.soft.spells.Damage.TYPE_MISS;
import art.soft.units.Attack;
import art.soft.units.Unit;
import static art.soft.units.Unit.MINI_MAP_VIS;

/**
 *
 * @author Артем
 */
public class mortraBlur extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean modif){
        u.status |= MINI_MAP_VIS;
    }

    @Override
    public int unitAttackThis(Unit u){
        if (random.nextBoolean(10 * level))
            Attack.type |= TYPE_MISS;
        return 0;
    }
}
