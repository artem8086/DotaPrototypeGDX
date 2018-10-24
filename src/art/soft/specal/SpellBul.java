package art.soft.specal;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.spells.Effect;
import art.soft.spells.Spell;
import art.soft.units.AnimData;
import art.soft.units.Unit;
import com.badlogic.gdx.math.MathUtils;

/**
 *
 * @author Artem
 */
public class SpellBul extends specToUnit {

    public Spell spell;
    
    public void Init( Unit u1, AnimData aData, int x1, int y1, Spell s, float speed, int visible){
        super.Init(0, 0, speed, aData, visible);

        uMove = null;
        x = u1.getUnitXD() + u1.getDopX();
        y = u1.getUnitYD() + u1.getDopY();
        
        setAnim( 0 );
        spell = s;
        setMovePos( x1, y1 );
    }

    public static SpellBul addSpellBul( Unit u, AnimData aData, int x, int y, Spell s, float speed, int visible){
        SpellBul b = (SpellBul) Game.postEffects.getEffect(SpellBul.class);
        b.Init(u, aData, x, y, s, speed, visible);
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
        if (spell!=null) {
            Effect e = spell.getCastEffect();
            if (e!=null) {
                e.castSpellToPos(xp, yp);
                e.castSpell();
            }
        }
        if (aData.Data.length<=1) {
            isCycle = false; return true;
        }
        setAnim(1);
        isCycle = false;
        return false;
    }

    @Override
    public void play( Graphics g, int x, int y ){
        setAngle( (int) (angle * MathUtils.radiansToDegrees) );
        super.play(g, x, y);
    }
}
