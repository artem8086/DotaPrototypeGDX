package maps.main.items;

import art.soft.multiplayer.PlayerGroup;
import art.soft.spells.Effect;
import art.soft.units.ULevelCreep;
import art.soft.units.Unit;
import art.soft.utils.selectors.SelectUnits;

/**
 *
 * @author Artem
 */
public class BattleFury extends Effect {

    private static final int radiusAOE = 200 * 200;

    @Override
    public void doEffectsStats(Unit u, boolean stack) {
        ULevelCreep uc = u.uLvl;
        uc.add_dmg += 65;
        uc.hpReg += 6;
        Unit.addPerMPReg += 1.5;
    }

    @Override
    public void doAttackModificator(Unit aim, int damage, int dmg){
        damage = (damage * 35) / 100;
        int i = spell.influents;
        Unit u = SelectUnits.setCircle(aim.px, aim.py, radiusAOE);
        PlayerGroup grp = owner.owners;
        while (u!=null) {
            if (u!=aim && !grp.groupInGroup(u.frendGRP) && owner.isInfluents(u, i)) {
                u.status |= Unit.BREAK_SPELL;
                u.dealsClearDamage(damage, owner);
            }
            u = SelectUnits.getNextUnit();
        }
    }

    @Override
    public Effect get2Modificator() {
        return owner.uLvl.getAttackType()==1 ? this : null;
    }
}
