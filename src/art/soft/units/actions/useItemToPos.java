package art.soft.units.actions;

import art.soft.spells.Effect;
import art.soft.units.Unit;
import static art.soft.units.Unit.CANT_USE_ITEM;

/**
 *
 * @author Artem
 */
public class useItemToPos extends moveToPos {

    private int radius, slot;

    public void setAimUnit(Unit u, int slot, int x, int y) {
        if ((u.status & CANT_USE_ITEM)==0) {
            this.x = x;
            this.y = y;
            this.slot = slot;
            Unit item = u.getItem(slot);
            if (item!=null && item.item!=null && item.item.spell!=null) {
                radius = item.item.spell.radiusS;
            } else radius = 0;
            postInit(u);
        }
    }

    @Override
    public boolean act (Unit u) {
        if (moveUnit(u, x, y) || radius<0 || u.getDist(x, y) < radius) {
            if ((u.status & CANT_USE_ITEM)==0) {
                Unit item = u.getItem(slot);
                if (item.item!=null) {
                    if (item.item.spell!=null) {
                        Effect e = item.item.spell.getEffect();
                        if (e!=null && e.castItem(item)) {
                            e.useItemToPos(u, item, slot, x, y);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
