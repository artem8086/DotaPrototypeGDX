package art.soft.units;

import art.soft.multiplayer.ClientServer;

/**
 *
 * @author Artem
 */
public class Action {

        // Пул "удалёных" эфектов
    private static Action buffActions, lastBuffered;

    public static final int canAutoAttack = 1;
    public static final int canInterruptAction = 2;
    public static final int deleteThisAction = 4;
    public static final int alreadyAttack = 8;

    public int flags;
    public Action next;

    public void Init (Unit u) {
        flags = Action.canInterruptAction;
    }

    public void setAutoAttack() {
        flags |= Action.canAutoAttack;
    }

    public void postInit (Unit u) {}

    public boolean act(Unit u) {
        return false;
    }

    public static void deleteAll(){
        buffActions = lastBuffered = null;
    }

    public static <T> T setAction(Unit u, Class<T> type, boolean add){
        if (u!=null) {
            Action a = u.actions;
            if (add) {
                if (a!=null && (a.flags & deleteThisAction)!=0) poolFirst(u);
                a = (Action) getAction(type);
                a.Init(u);
                if (u.lastAct==null) {
                    u.actions = u.lastAct = a;
                } else {
                    u.lastAct.next = a;
                    u.lastAct = a;
                }
                return (T) a;
            } else
            if (a==null || (a.flags & canInterruptAction)!=0) {
                poolActions(u);
                a = (Action) getAction(type);
                a.Init(u);
                a.next = u.actions;
                u.lastAct = u.actions = a;
                return (T) a;
            }
        }
        return null;
    }

    public static boolean canAutoAttack (Unit u) {
        if (u.actions==null) {
            int pb = u.owners.playerBit;
            int n = 0;
            boolean caa = true;
            while (pb!=0) {
                if ((pb & 1)!=0) {
                    if (!ClientServer.players[n].canAutoAttack) {
                        caa = false;
                    }
                }
                n ++;
                pb >>= 1;
            }
            return caa;
        } else {
            return (u.actions.flags & canAutoAttack)!=0;
        }
    }

    public static <T> T addAutoAttack(Unit u, Class<T> type){
        if (u!=null) {
            Action a = u.actions;
            if (a==null || (a.flags & canInterruptAction)!=0) {
                if (a!=null && (a.flags & deleteThisAction)!=0) poolFirst(u);
                a = (Action) getAction(type);
                a.Init(u);
                a.next = u.actions;
                if (u.actions==null) u.lastAct = a;
                u.actions = a;
            }
            return (T) a;
        }
        return null;
    }
    
    public static <T> T getAction (Class<T> type) {
        Action a = buffActions;
        Action p = null;
        while( a!=null ){
            if( type==a.getClass() ){
                if( a==lastBuffered ) lastBuffered = p;
                if( p==null ) buffActions = a.next;
                else p.next = a.next;
                a.next = null;
                return (T) a;
            }
            p = a;
            a = a.next;
        }
        try {
            T t = type.newInstance();
            return t;
        }catch( InstantiationException ex) {} catch (IllegalAccessException ex) {}
        return null;
    }

    
    public static void poolActions(Unit u){
        if (u!=null) {
            if (u.actions!=null) {
                if (buffActions==null) buffActions = u.actions;
                else lastBuffered.next = u.actions;
                lastBuffered = u.lastAct;
                u.actions = u.lastAct = null;
            }
        }
    }

    public static void poolFirst(Unit u){
        if (u!=null) {
            Action a = u.actions;
            if (a!=null) {
                if (a.next==null) {
                    u.actions = u.lastAct = null;
                } else
                    u.actions = a.next;
                if (buffActions==null) lastBuffered = buffActions = a;
                else {
                    lastBuffered.next = a;
                    lastBuffered = a;
                }
                a.next = null;
            }
        }
    }

    public static void acts(Unit u){
        Action a = u.actions;
        Action t;
        while (a!=null) {
            if (a.act(u)) {
                poolFirst(u);
                a = u.actions;
                if (a!=null) a.postInit(u);
            } else
                a = null;
        }
    }
}
