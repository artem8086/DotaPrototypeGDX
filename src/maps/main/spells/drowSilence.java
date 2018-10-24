package maps.main.spells;

import art.soft.Game;
import art.soft.spells.Effect;
import art.soft.units.Unit;
import static art.soft.units.Unit.IS_ALIVE;
import static art.soft.units.Unit.IS_BULDING;
import static art.soft.units.Unit.IS_SILENCE;

/**
 *
 * @author Артем
 */
public class drowSilence extends Effect {

    private static final int rangeAOE = 160 * 160;

    @Override
    public void doEffectsStats(Unit u, boolean modif){
        u.status |= IS_SILENCE;
        isDelete = true;
    }

    @Override
    public void castSpellToPos(int x, int y){
        Unit u = (Unit) Game.allUnits.first;
        int f = owner.frendGRP;
        setCooldownTime(2000 + 1000 * level);
        while (u!=null) {
            if ((u.status & IS_BULDING)==0 && (u.status & IS_ALIVE)!=0 && !u.owners.groupInGroup(f) && u.getDist(x, y)<rangeAOE) {
                addUnicUnitEffect(u).startCooldown();
            }
            u = (Unit) u.next;
        }
        setCooldownTime(9000);
    }
}
