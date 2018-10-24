package art.soft.specal;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.spells.Damage;
import static art.soft.spells.Damage.DONT_BREAK_SPELL;
import art.soft.units.*;
import static art.soft.units.Unit.IS_ALIVE;
import com.badlogic.gdx.math.MathUtils;

/**
 *
 * @author Артем
 */
public class Bullet extends specToUnit {

    public Damage damage;
    //public Unit uKick;
    
    public void Init( Unit u1, AnimData aData, Unit u, Damage damage, float speed, int visible){
        super.Init(0, 0, speed, aData, visible);
        
        x = u1.getUnitXD() + u1.getDopX();
        y = u1.getUnitYD() + u1.getDopY();
        
        setAnim( 0 );
        uMove = u; //uKick = u1;
        this.damage = damage;
        setMovePos( uMove.getUnitXD(), uMove.getUnitYD()-uMove.uData.body_height );
    }
    
    public static Bullet addBullet( Unit u, AnimData aData, Unit aim, Damage damage, float speed, int visible){
        Bullet b = (Bullet) Game.postEffects.getEffect(Bullet.class);
        b.Init(u, aData, aim, damage, speed, visible);
        return b;
    }

    @Override
    public boolean incAnim(){
        if (incAnm()) if( !isCycle )
            return true;
        return false;
    }
    @Override
    public boolean endMove(){
        if( uMove!=null && (uMove.status & IS_ALIVE)!=0 ){
            if (damage!=null) {
                if ((damage.level & DONT_BREAK_SPELL)==0) uMove.status |= Unit.BREAK_SPELL;
                damage.setDamege(uMove);
            }
            uMove = null;
            //System.out.println( "Deals damage!" );
        }
        if (aData.Data.length<=1) {
            isCycle = false; return true;
        }
        setAnim( 1 );
        isCycle = false;
        return false;
    }
    @Override
    public void play( Graphics g, int x, int y ){
        setAngle( (int) (angle * MathUtils.radiansToDegrees) );
        super.play(g, x, y);
    }
}
