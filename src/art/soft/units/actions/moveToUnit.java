package art.soft.units.actions;

import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class moveToUnit extends moveToAim {

    public Unit aim;

    public void setAimUnit(Unit u, Unit aim) {
        this.aim = aim;
        postInit(u);
    }

    @Override
    public boolean act(Unit u) {
        boolean r = moveUnit(u, aim, u.uData.softRadius);
        if (r) aim = null;
        return r;
    }
}
