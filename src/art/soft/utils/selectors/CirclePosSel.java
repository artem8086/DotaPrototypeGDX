package art.soft.utils.selectors;

import art.soft.units.Unit;
import static art.soft.utils.selectors.SelectUnits.next;

/**
 *
 * @author Artem
 */
public class CirclePosSel extends SelectUnits {
    
    final static CirclePosSel selector = new CirclePosSel();

    @Override
    Unit getNext() {
        Unit u = next;
        while (u!=null) {
            next = (Unit) u.next;
            if (r<0 || u.getDist(x, y)<r) {
                return u;
            }
            u = next;
        }
        return null;
    }
}
