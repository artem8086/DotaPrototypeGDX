package maps.main.items;

import art.soft.spells.AttackModif;
import art.soft.spells.Effect;

/**
 *
 * @author Artem
 */
public class Skadi extends AttackModif {

    @Override
    public Effect get2Modificator() {
        effect.setCooldownTime(owner.uLvl.getAttackType()==1 ? 5000 : 3000);
        return effect;
    }
}
