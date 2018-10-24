package maps.main.items;

import art.soft.Game;
import static art.soft.multiplayer.ClientServer.random;
import art.soft.specal.flyText;
import art.soft.spells.Effect;
import art.soft.units.Attack;
import art.soft.units.ULevelCreep;
import art.soft.units.Unit;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Artem
 */
public class Crit extends Effect {

    public int chance, power, damage;

    @Override
    public Effect copy(Effect e){
        Crit c = (Crit) super.copy(e);
        c.chance = chance;
        c.power = power;
        c.damage = damage;
        return c;
    }

    @Override
    public void doEffectsStats(Unit u, boolean stack) {
        ULevelCreep uc = u.uLvl;
        uc.add_dmg += damage;
    }

    @Override
    public int startDamageAim(Unit u, Unit aim){
        if (random.nextBoolean(chance)) {
            int d = (Attack.mDmg * power) / 100;
            Attack.tDmg += d;
            if (d>0) ((flyText) Game.postEffects.getEffect(flyText.class)).Init( owner, Color.RED, "+"+d, owner.visGRP | aim.visGRP );
            return modif;
        }
        return 0;
    }
}
