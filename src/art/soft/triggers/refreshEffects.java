package art.soft.triggers;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.gui.buttons.inGame.MiniMap;
import art.soft.map.GameMap;
import art.soft.multiplayer.*;
import art.soft.spells.Effect;
import art.soft.units.*;
import art.soft.utils.Rectangle;
import static art.soft.units.Unit.IS_ALIVE;
import static art.soft.units.Unit.MINI_MAP_VIS;

/**
 *
 * @author Артем
 */
public class refreshEffects extends gameTimer {
    
    public static MiniMap miniMap;
    
    public static final int MAX_VISIBLE_UNITS = 512;
    
    public static int numVisUnits;

    public static int cyc, sec, min, hours; // переменные времени
    public static boolean refreshEffect = true;
    
    public refreshEffects(int gameCycle) {
        super(gameCycle, true); // Таймер для обновления эффектов каждые 100 мс
    }

    @Override
    public void callTimer(){
        refreshEffect = true;
        cyc ++;
        if (cyc==Game.gCYC) {
            Game.secondTick = true;
            cyc = 0;
            sec ++;
            if (sec==60) {
                sec = 0;
                min ++;
                if (min==60) {
                    hours ++;
                }
            }
        }
    }

    public static void refreshAll(Graphics g){
        Rectangle r = Game.curCam.rect;
        
        // Очистка группы VIS и прорисовка статуса HP юнита
        Unit p = Game.visUnits;
        Unit u1;
        Unit uh = null;
        Unit u2 = Game.unitSelectNow;
        if (u2!=null && u2.ID<0) Game.unitSelectNow = u2 = null;
        while (p!=null) {
            u1 = p;
            if ((p.status & IS_ALIVE)!=0 && u1!=u2) p.drawHP(g, false);
            p = p.VISnext;
            u1.VISnext = null;
        }
        if (u2!=null) u2.drawHP(g, Game.getInfo);
        Game.CShost.selectEnd();
        Game.visUnits = null;

        g.end();
        g.begin();
        g.setProjectionMatrix(Game.hudCam.combined);
        if (Game.HUD==Game.inGame) miniMap.preDraw(g);
        PlayerGroup pg = Game.yourPlayer.owner;
        
        // Выполнение действий всех юнитов
        // и отрисовка их на карте и миникарте
        // Проверка и сортировка видимых инитов
        u1 = (Unit) Game.allUnits.first;
        numVisUnits = 0;
        p = null;
        while (u1!=null) {
            u2 = (Unit) u1.next;
            if (u1.unitAct() && pg.groupInGroup(u1.isVisible)) {
                if (r.contains(u1.px, u1.py)) addToVis(u1);
                if ((u1.status & IS_ALIVE)!=0 && (u1.status & MINI_MAP_VIS)!=0 && Game.HUD==Game.inGame) miniMap.drawUnit(g, u1);
            }
            u1 = u2;
        }
        if (Game.HUD==Game.inGame) miniMap.postDraw(g);
        //gr.setTransform( gui );
        
        // Проверка автоатаки и обновление эффектов всех юнитов
        p = null;
        if (refreshEffect) {
            for (Player pl : ClientServer.players) {
                if (pl!=null) pl.refreshTrigger();
            }
            PlayerGroup fg = Game.yourPlayer.FrendPlayer;
            GameMap m = Game.map;
            u1 = (Unit) Game.allUnits.first;
            while( u1!=null ){
                u2 = (Unit) u1.next;
                if( (u1.status & IS_ALIVE)!=0 ){
                    u1.refreshEffets();
                } else Effect.refreshStats(u1);
                //if( u1.inInvis ) u1.isVisible = u1.visGRP; else u1.isVisible = -1;
                u1.isVisible = u1.visGRP;
                u1 = u2;
            }
            u1 = (Unit) Game.allUnits.first;
            while( u1!=null ){
                Attack a = u1.uLvl.getCurAttack();
                u2 = (Unit) u1.next;
                while (u2!=null) {
                    int x = u1.px - u2.px;
                    int y = u1.py - u2.py;
                    int dist = x * x + y * y;

                    boolean alv = true;
                    if ((u1.status & IS_ALIVE)!=0) {
                        if ((u2.isVisible & u1.visGRP)!=u1.visGRP) {
                            if (!u1.owners.groupInGroup(u2.inInvis)) {
                                if (u1.rVis > dist && m.rayCastVis(u1.px, u1.py, u2.px, u2.py, u1.getUnitHeight(), u1.visGRP))
                                    u2.isVisible |= u1.visGRP;
                            }
                        }
                    } else alv = false;
                    if ((u2.status & IS_ALIVE)!=0) {
                        if ((u1.isVisible & u2.visGRP)!=u2.visGRP) {
                            if (!u2.owners.groupInGroup(u1.inInvis)) {
                                if (u2.rVis > dist && m.rayCastVis(u2.px, u2.py, u1.px, u1.py, u2.getUnitHeight(), u2.visGRP))
                                    u1.isVisible |= u2.visGRP;
                            }
                        }
                    } else alv = false;

                    if (alv) {
                        u1.refreshAurs( u2, dist );
                        u2.refreshAurs( u1, dist );

                        if (u2.canAutoAttack(u1)) {
                            if (u2.canAutoAttack && u2.owners.groupInGroup(u1.isVisible) && dist<u2.uLvl.autoAttackRad) {
                                Attack a2 = u2.uLvl.getCurAttack();
                                if (a2.autoDist>dist) {
                                    //u2.setAttackUnit( u1, false );
                                    a2.aim = u1;
                                    a2.autoDist = dist;
                                }
                            }
                        }
                        if (u1.canAutoAttack(u2)) {
                            if (u1.canAutoAttack && u1.owners.groupInGroup(u2.isVisible) && dist<u1.uLvl.autoAttackRad) {
                                if (a.autoDist>dist) {
                                    //u1.setAttackUnit( u2, false );
                                    a.aim = u2;
                                    a.autoDist = dist;
                                }
                            }
                        }
                    }
                    u2 = (Unit) u2.next;
                }
                if (u1.canAutoAttack) u1.setAttackUnit(a.aim, false, false);
                u1.postClear();
                u1 = (Unit) u1.next;
            }
        }
        refreshEffect = false;
    }

    public static void addToVis( Unit u ){
        if (numVisUnits<MAX_VISIBLE_UNITS) {
            numVisUnits ++;
            // Добовляет юнита к группе VIS сортируя его по Y
            Unit p = Game.visUnits;
            Unit pred = null;
            if( p!=null ){
                if( p.py > u.py ){
                    Game.visUnits = u;
                    u.VISnext = p;
                    return;
                }
                pred = p;
                p = p.VISnext;

            }
            while( p!=null ){
                if( p.py > u.py ){
                    pred.VISnext = u;
                    u.VISnext = p;
                    return;
                }
                pred = p;
                p = p.VISnext;
            }
            if( Game.visUnits==null ) Game.visUnits = u;
            else pred.VISnext = u;
        }
    }
}
