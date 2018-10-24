package art.soft.units.actions;

import art.soft.Game;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class DropItemToPos extends moveToPos {

    private int item;

    @Override
    public boolean act (Unit u) {
        if (moveUnit(u, x, y) || u.getDist(x, y) < u.uData.softRadius) {
            Unit it = u.dropItem(item);
            if (it!=null) {
                it.setPos(x, y);
                Game.addUnit(it);
            }
            return true;
        }
        return false;
    }

    public void setAimXY (Unit u, int item, int x, int y) {
        this.item = item;
        this.x = x;
        this.y = y;
        postInit(u);
    }
}
