package art.soft.gui;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.units.AnimData;
import art.soft.units.Animation;
/**
 *
 * @author Артем
 */
public class intro {

    public boolean start = true;
    public Animation intr;
    public long curTime;
    public final int widthAnim = 512;

    public intro() { 
        super();

        intr = new Animation(AnimData.load(""));
        intr.setAnim(0);
        intr.isCycle = false;
        intr.k = Game.w / widthAnim;
        start = Game.settings.showIntro;
    }

    public void draw(Graphics g){
        if (start) {
            g.begin();
            intr.play(g, Game.w >> 1, Game.h >> 1);
            g.end();
            start = !intr.incAnm();
            if (Game.keyPressed!=0 || Game.mbut1 || Game.mbut2) start = false;
        } else {
            dispose();
            Game.Intro = null;
            Loading.LoadScreenDraw();
        }
    }

    public void dispose(){
        intr.aData.dispose();
    }
}
