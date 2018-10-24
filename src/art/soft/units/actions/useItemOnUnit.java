package art.soft.units.actions;

import art.soft.spells.Effect;
import art.soft.units.Unit;
import static art.soft.units.Unit.CANT_USE_ITEM;

/**
 *
 * @author Artem
 */
public class useItemOnUnit extends moveToUnit {

    private int radius, slot;

    public void setAimUnit(Unit u, Unit aim, int slot) {
        if ((u.status & CANT_USE_ITEM)==0) {
            this.aim = aim;
            this.slot = slot;
            Unit item = u.getItem(slot);
            if (item!=null && item.item!=null && item.item.spell!=null) {
                radius = item.item.spell.radiusS;
            } else radius = 0;
            postInit(u);
        }
    }

    @Override
    public boolean meetWithUnit(Unit u, Unit aim) {
        if ((u.status & CANT_USE_ITEM)==0) {
            Unit item = u.getItem(slot);
            if (item.item!=null) {
                if (item.item.spell!=null) {
                    Effect e = item.item.spell.getEffect();
                    if (e!=null && e.castItem(item)) {
                        e.useItemOnUnit(u, item, slot, aim);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean act(Unit u) {
        boolean r = moveUnit(u, aim, radius);
        if (r) aim = null;
        return r;
    }
}
