package art.soft.units.actions;

import art.soft.units.Action;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class moveToAim extends Action {

    
    public boolean meetWithUnit(Unit u, Unit aim) {
        if (u.owners.groupInGroup(aim.canBeTaken)) {
            if (u.giveItem(aim)) aim.removeFromMap();
            aim = null;
            return true;
        }
        return false;
    }

    public boolean moveUnit(Unit u, Unit aim, int dist) {
        if (aim!=null && aim.ID>=0) {
            if ((aim.isVisible & u.owners.playerBit)==0) {
                return true;
            } else {
                if (dist<0 || u.getDist(aim)<dist) {
                    u.standAnim();
                    return meetWithUnit(u, aim);
                } else {
                    if ((u.status & Unit.CANT_MOVE)==0) {
                        if (u.setNewPos(aim.px, aim.py)) {
                            u.standAnim();
                        } else u.setMoveAnim();
                    } else {
                        u.setDirToUnit(aim);
                        u.setMoveAnim();
                    }
                }
            }
            u.incAnm();
            return false;
        }
        return true;
    }

}
