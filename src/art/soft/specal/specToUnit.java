package art.soft.specal;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.multiplayer.PlayerGroup;
import art.soft.units.AnimData;
import art.soft.units.Unit;
import com.badlogic.gdx.math.MathUtils;

/**
 *
 * @author Artem
 */
public class specToUnit extends specEffect {

    public int xp, yp;
    public float speed, angle;
    public Unit uMove;

    public void Init( int x, int y, float speed, AnimData aData, int visible ){
        super.Init(x, y, aData, visible);
        this.speed = speed;
        //isVisible = true;
        //setAnim( 0, 900 );
    }

    @Override
    public boolean incAnim() {
        incAnm();
        return false;
    }

    @Override
    public boolean move() {
        float s = speed * Game.deltaT;
        x += s * MathUtils.cos(angle);
        y += s * MathUtils.sin(angle);
        if( uMove!=null ){
            setMovePos( uMove.getUnitXD(), uMove.getUnitYD()-uMove.uData.body_height );
        }
        if (isEndMove(s)) return endMove();
        return false;
    }

    public boolean isEndMove(float s) {
        float dx = xp - x;
        float dy = yp - y;
        if (dx<0) dx = - dx;
        if (dy<0) dy = - dy;
        if (dx<=s && dy<=s) {
            return true;
        }
        return false;
    }

    public boolean endMove() {
        return true;
    }
    public void setMovePos(int xt, int yt) {
        angle = MathUtils.atan2(yt - (int) y, xt - (int) x);
        xp = xt; yp = yt;
    }

    @Override
    public void relese() {
        uMove = null;
    }
}
