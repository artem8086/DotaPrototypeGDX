package maps.main.items;

import static art.soft.multiplayer.ClientServer.random;
import art.soft.spells.Effect;
import art.soft.units.Attack;
import art.soft.units.ULevelCreep;
import art.soft.units.ULevelHero;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class Vanguard extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean stack) {
        ULevelCreep uc = u.uLvl;
        uc.max_hp += 250;
        uc.hpReg += 6;
    }
    
    @Override
    public int unitAttackThis(Unit u){
        if (random.nextBoolean(80)) {
            Attack.mDmg -= owner.uLvl.getAttackType()==1 ? 40 : 20;
            return modif;
        }
        return 0;
    }
}
