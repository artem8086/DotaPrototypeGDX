package art.soft.triggers;

import art.soft.Game;

/**
 *
 * @author Артем
 */
public class gameTimer {

    public static gameTimer timers, lastTimer;
    public static long gameTime, startTime;
    
    public long nextFrame;
    public int deltaFTime;
    public boolean isCycle;
    public gameTimer next;
    
    public gameTimer( int dFTime, boolean isCycle ){
        nextFrame = gameTime;
        deltaFTime = dFTime;
        if( !(this.isCycle = isCycle) ){
            nextFrame += dFTime;
        }
    }

    public boolean incTimer(){
        if( gameTime>=nextFrame ){
            callTimer();
            if( isCycle ){
                nextFrame += deltaFTime;
            } else return true;
        }
        return false;
    }
    
    public static void Init(){
        startTime = System.currentTimeMillis();
        gameTime = 0;
    }

    public static void deleteAll() {
        timers = lastTimer = null;
    }

    public static void incAllTimers(){
        Game.secondTick = false;
        gameTimer t = timers;
        gameTimer pred = null;
        gameTime = System.currentTimeMillis() - startTime;
        while( t!=null ){
            if( t.incTimer() ){
                // удаление неиспользуемого таймера
                if( pred==null ) timers = t.next;
                else{
                    pred.next = t.next;
                    if( t.next==null ) lastTimer = pred;
                }
            }
            pred = t;
            t = t.next;
        }
    }
    
    public void callTimer(){}
    
    public static void startTimer( gameTimer t ){
        if( t!=null ){
            if( timers==null ){
                lastTimer = timers = t;
            } else {
                lastTimer.next = t;
                lastTimer = t;
            }
        }
    }

    
}
