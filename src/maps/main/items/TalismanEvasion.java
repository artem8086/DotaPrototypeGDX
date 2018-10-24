package maps.main.items;

import static art.soft.multiplayer.ClientServer.random;
import static art.soft.spells.Damage.TYPE_MISS;
import art.soft.spells.Effect;
import art.soft.units.Attack;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class TalismanEvasion extends Effect {

    @Override
    public int unitAttackThis(Unit u){
        if (random.nextBoolean(25))
            Attack.type |= TYPE_MISS;
        return 0;
    }
}
