package art.soft.units.actions;

import art.soft.units.Action;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class Stand extends Action {

    @Override
    public void Init (Unit u) {
        flags = Action.canInterruptAction | Action.deleteThisAction;
        if (Action.canAutoAttack(u))
            flags |= Action.canAutoAttack;
        postInit(u);
    }

    @Override
    public void postInit (Unit u) {
        u.standAnim();
    }

    @Override
    public boolean act (Unit u) {
        u.incAnm();
        return false;
    }
}
