package art.soft.units.actions;

import art.soft.units.Action;
import art.soft.units.Unit;
import static art.soft.units.Unit.IS_ALIVE;

/**
 *
 * @author Artem
 */
public class moveToPos extends Action {

    public int x, y;

    @Override
    public void postInit (Unit u) {
        if ((u.status & IS_ALIVE)!=0) {
            //u.setMovePos(x, y, true);
            u.setMoveAnim();
            if ((u.status & Unit.CANT_ROTATE)==0) {
                u.setDirFromVec(x - u.px, y - u.py);
            }
        }
    }

    public static boolean moveUnit(Unit u, int x, int y) {
        u.incAnm();
        if ((u.status & Unit.CANT_MOVE)==0) {
            if (u.setNewPos(x, y)) {
                u.standAnim();
            } else u.setMoveAnim();
            return x==u.px && y==u.py;
        } else return false;
    }

    @Override
    public boolean act (Unit u) {
        if ((flags & canAutoAttack)==0 || next!=null) {
            return moveUnit(u, x, y);
        } else {
            if (x==u.px && y==u.py) {
                u.standAnim();
                u.incAnm();
            } else {
                u.setMoveAnim();
                moveUnit(u, x, y);
            }
            return false;
        }
    }

    public void setAimXY (Unit u, int x, int y) {
        this.x = x;
        this.y = y;
        postInit(u);
    }
}
