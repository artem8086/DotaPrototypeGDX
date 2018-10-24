package art.soft.specal;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.multiplayer.PlayerGroup;
import art.soft.units.*;
import com.badlogic.gdx.math.MathUtils;

/**
 *
 * @author Артем
 */
public class specEffect extends Animation {

    public int visible;
    public float x, y;

    public void Init( int x, int y, AnimData aData, int visible ){
        this.aData = aData;
        this.visible = visible;
        this.x = x;
        this.y = y;
        isCycle = true;
        reset();
        //isVisible = true;
        //setAnim( 0, 900 );
    }
    public boolean draw(Graphics g) {
        boolean d = move();
        if ((visible & PlayerGroup.yourPlayerBit)!=0) {
            int xt = (int) x;
            int yt = (int) y;
            if (Game.curCam.rect.contains(xt, yt))
                play(g, xt, yt);
        }
        return incAnim() | d;
    }

    public boolean incAnim() {
        return incAnm();
    }

    public boolean move() {
        return true;
    }

}
