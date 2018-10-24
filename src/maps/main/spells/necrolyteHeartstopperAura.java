package maps.main.spells;

import art.soft.Game;
import art.soft.spells.Effect;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class necrolyteHeartstopperAura extends Effect {

    @Override
    public void doEffectsDamage(Unit u, boolean modif){
        if (level>0) {
            float dmg = u.uLvl.max_hp * Game.gCYCo * (0.05f + 0.04f * level);
            u.dealsMagicDamage((int) dmg, owner);
            if (u!=owner) isDelete = true;
        }
    }
}
