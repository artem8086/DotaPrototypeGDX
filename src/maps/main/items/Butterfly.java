package maps.main.items;

import static art.soft.multiplayer.ClientServer.random;
import static art.soft.spells.Damage.TYPE_MISS;
import art.soft.spells.Effect;
import art.soft.units.Attack;
import art.soft.units.ULevelCreep;
import art.soft.units.ULevelHero;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class Butterfly extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean stack) {
        ULevelCreep uc = u.uLvl;
        uc.add_dmg += 30;
        uc.add_AS += 30;
        if (uc instanceof ULevelHero) {
            ULevelHero ul = (ULevelHero) uc;
            ul.t_agl += 30;
        }
    }

    
    @Override
    public int unitAttackThis(Unit u){
        if (random.nextBoolean(25))
            Attack.type |= TYPE_MISS;
        return 0;
    }
}
