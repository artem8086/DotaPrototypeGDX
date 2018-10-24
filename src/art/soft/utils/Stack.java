package art.soft.utils;

/**
 *
 * @author Artem
 */
public class Stack {

    public StackObj first, last;

    public <T> T  getFirst(Class<T> type) {
        if (first!=null) {
            StackObj g = first;
            if ((first = g.next)==null) last = null;
            g.next = null;
            return (T) g;
        } else {
            try {
                T s = type.newInstance();
                return s;
            }catch( InstantiationException ex) {} catch (IllegalAccessException ex) {}
        }
        return null;
    }

    public <T> T  get(Class<T> type) {
        StackObj s = first;
        StackObj p = null;
        while( s!=null ){
            if (type.isInstance(s)) {
                remove(p, s);
                return (T) s;
            }
            p = s;
            s = s.next;
        }
        try {
            T t = type.newInstance();
            return t;
        }catch( InstantiationException ex) {} catch (IllegalAccessException ex) {}
        return null;
    }
    
    public void delete() {
        StackObj stack = first;
        while (stack!=null) {
            stack.relese();
            stack = stack.next;
        }
        first = last = null;
    }

    public void deleteFast() {
        first = last = null;
    }

    public void pool (Stack pool) {
        if (pool.last==null) pool.first = first;
        else pool.last.next = first;
        pool.last = last;
        first = last = null;
    }

    public void add(StackObj obj) {
        if( first==null ){
            last = first = obj;
        } else {
            last.next = obj;
            last = obj;
        }
        obj.next = null;
    }

    public void remove(StackObj prev, StackObj obj) {
        StackObj ne = obj.next;
        if (ne==null) last = prev;
        if (prev==null) first = ne;
        else prev.next = ne;
        obj.next = null;
    }

    public void delete(StackObj prev, StackObj obj) {
        remove(prev, obj);
        obj.relese();
    }

    public boolean remove(StackObj obj) {
        StackObj stack = first;
        StackObj prev = null;
        while (stack!=null) {
            if (stack==obj) {
                remove(prev, obj);
                return true;
            }
            prev = stack;
            stack = stack.next;
        }
        return false;
    }
}
