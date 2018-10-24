package maps.main.items;

import art.soft.spells.Effect;
import art.soft.units.ULevelCreep;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class AssaultCuirass extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean modif) {
        ULevelCreep uc = u.uLvl;
        if (u.owners.groupInGroup(owner.frendGRP)) {
            Icon = spell.Icon;
            uc.add_AS += 20;
            uc.t_am += 5;
            attrib |= BUFF_EFFECT;
        } else {
            Icon = owner.owners.groupInGroup(u.visGRP) ? spell.Icon : null;
            attrib &= ~ BUFF_EFFECT;
            uc.t_am -= 5;
        }
        isDelete = true;
    }
}
