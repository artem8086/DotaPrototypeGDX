package art.soft.units.actions;

import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class DropItemToUnit extends moveToUnit {

    private int item;

    public void setAimUnit(Unit u, int item, Unit aim) {
        this.aim = aim;
        this.item = item;
        postInit(u);
    }

    @Override
    public boolean meetWithUnit(Unit u, Unit aim) {
        Unit i = u.dropItem(item);
        if (i!=null) {
            if (!aim.giveItem(i)) u.randomDropItem(i);
        }
        return true;
    }
}
