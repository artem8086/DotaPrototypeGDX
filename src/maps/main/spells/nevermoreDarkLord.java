/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.main.spells;

import art.soft.spells.Effect;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class nevermoreDarkLord extends Effect{
    
    @Override
    public void doEffectsStats(Unit u, boolean modif){
        if (level>0) {
            u.uLvl.t_am -= 15 * level;
        }
        isDelete = true;
    }
}
