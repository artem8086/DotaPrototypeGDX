/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class PoorManShield extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean stack) {
        ULevelCreep uc = u.uLvl;
        if (uc instanceof ULevelHero) {
            ((ULevelHero) uc).t_agl += 6;
        }
    }
    
    @Override
    public int unitAttackThis(Unit u){
        if (u.uLvl instanceof ULevelHero || random.nextBoolean(60)) {
            Attack.mDmg -= owner.uLvl.getAttackType()==1 ? 20 : 10;
            return modif;
        }
        return 0;
    }
}
