package art.soft.specal;

import art.soft.Graphics;
import art.soft.utils.Stack;

/**
 *
 * @author Artem
 */
public class poolEffects extends Stack {

            // Пул "удалёных" эфектов
    private static final Stack pool = new Stack();

    public specEffect getEffects() {
        return (specEffect) first;
    }

    public static void deletePool() {
        pool.deleteFast();
    }
    
    public <T> T getEffect (Class<T> type) {
        T s = pool.get(type);
        if (s!=null) add((specEffect) s);
        return s;
    }

    public void refreshEffects( Graphics g ){
        specEffect se = (specEffect) first;
        specEffect pred = null;
        specEffect ne;
        while (se!=null) {
            ne = (specEffect) se.next;
            if( se.draw( g ) ){
                delete(pred, se);
                pool.add(se);
            } else
                pred = se;
            se = ne;
        }
    }
}
