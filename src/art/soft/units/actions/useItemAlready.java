package art.soft.units.actions;

import art.soft.spells.Effect;
import art.soft.units.Action;
import art.soft.units.Unit;
import static art.soft.units.Unit.CANT_USE_ITEM;

/**
 *
 * @author Artem
 */
public class useItemAlready extends Action {

    private int slot;

    public void setItem(int slot) {
        this.slot = slot;
    }

    @Override
    public boolean act(Unit u) {
        if ((u.status & CANT_USE_ITEM)==0) {
            Unit item = u.getItem(slot);
            if (item!=null) {
                if (item.item!=null && item.item.spell!=null) {
                    Effect e = item.item.spell.getEffect();
                    if (e!=null && e.castItem(item)) {
                        e.useItemAlready(u, item, slot);
                    }
                } else {
                    u.randomDropItem(u.dropItem(slot));
                }
            }
        }
        return true;
    }
}
