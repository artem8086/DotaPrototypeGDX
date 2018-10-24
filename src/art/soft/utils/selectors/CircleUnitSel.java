package art.soft.utils.selectors;

import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
class CircleUnitSel extends SelectUnits {
    
    final static CircleUnitSel selector = new CircleUnitSel();

    @Override
    Unit getNext() {
        Unit u = next;
        while (u!=null) {
            next = (Unit) u.next;
            if (unit.isInfluents(u, influents) && (r<0 || unit.getDist(u)<r)) {
                return u;
            }
            u = next;
        }
        return null;
    }
}