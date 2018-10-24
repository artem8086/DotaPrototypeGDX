package maps.main.items;

import art.soft.spells.Effect;
import art.soft.spells.Spell;
import art.soft.units.Unit;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class Radiance extends Effect {

    @Override
    public Image getIcon() {
        return (spell.attrib & Spell.AURA_DISABLED)==0 ? null : Icon;
    }

    @Override
    public void doEffectsStats(Unit u, boolean stack) {
        u.uLvl.add_dmg += 60;
    }

    @Override
    public void useItemAlready(Unit owner, Unit item, int slot) {
        spell.attrib ^= Spell.AURA_DISABLED;
    }
}
