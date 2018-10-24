package maps.main.spells;

import art.soft.Game;
import static art.soft.multiplayer.ClientServer.random;
import art.soft.specal.flyText;
import art.soft.spells.Effect;
import art.soft.units.Attack;
import art.soft.units.Unit;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Артем
 */
public class mortraCoupDeGrace extends Effect {

    @Override
    public int startDamageAim(Unit u, Unit aim){
        if (random.nextBoolean(17)) {
            int d = (int) (Attack.mDmg * (1.5f + 1f * level));
            Attack.tDmg += d;
            if (d>0) ((flyText) Game.postEffects.getEffect(flyText.class)).Init( owner, Color.RED, "+"+d, owner.visGRP | aim.visGRP );
            return modif;
        }
        return 0;
    }
}
