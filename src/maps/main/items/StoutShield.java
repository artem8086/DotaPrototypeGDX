package maps.main.items;

import static art.soft.multiplayer.ClientServer.random;
import art.soft.spells.Effect;
import art.soft.units.Attack;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class StoutShield extends Effect {

    @Override
    public int unitAttackThis(Unit u){
        if (random.nextBoolean(60)) {
            Attack.mDmg -= owner.uLvl.getAttackType()==1 ? 20 : 10;
            return modif;
        }
        return 0;
    }
}
