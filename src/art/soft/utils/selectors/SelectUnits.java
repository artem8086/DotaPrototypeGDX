package art.soft.utils.selectors;

import art.soft.Game;
import art.soft.units.Unit;
import art.soft.utils.Rectangle;

/**
 *
 * @author Artem
 */
public abstract class SelectUnits {

    static int x, y, r, influents;
    static Unit unit, next;
    static SelectUnits selector;
    static Rectangle rect;

    public static Unit setCircle(Unit u, int i, int radius) {
        next = (Unit) Game.allUnits.first;
        selector = CircleUnitSel.selector;
        influents = i;
        r = radius;
        unit = u;
        return selector.getNext();
    }

    public static Unit setCircle(int x1, int y1, int radius) {
        next = (Unit) Game.allUnits.first;
        selector = CirclePosSel.selector;
        x = x1;
        y = y1;
        r = radius;
        return selector.getNext();
    }
 
    public static Unit getNextUnit() {
        return selector.getNext();
    }

    abstract Unit getNext();
}
