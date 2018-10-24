package maps.main.spells;

import art.soft.Game;
import art.soft.spells.Effect;
import art.soft.units.Unit;
import static art.soft.units.Unit.IS_ALIVE;
import static art.soft.units.Unit.IS_BULDING;

/**
 *
 * @author Артем
 */
public class svenGreatCleave extends Effect {

    private static final int radiusAOE = 200 * 200;

    @Override
    public void doAttackModificator(Unit aim, int damage, int dmg){
        Unit u = (Unit) Game.allUnits.first;
        int f = owner.frendGRP;
        while (u!=null) {
            if (aim!=u && (u.status & IS_BULDING)==0 && (u.status & IS_ALIVE)!=0 && !u.owners.groupInGroup(f) && aim.getDist(u)<radiusAOE) {
                u.dealsClearDamage((damage * (20 + 10 * level)) / 100, owner);
            }
            u = (Unit) u.next;
        }
    }
}
