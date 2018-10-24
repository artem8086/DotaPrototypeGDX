package maps.main.items;

import art.soft.Game;
import art.soft.specal.flyText;
import art.soft.spells.Effect;
import art.soft.units.Unit;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Artem
 */
public class OrbOfVenomEff extends Effect {


    @Override
    public void doEffectsDamage(Unit u, boolean stack){
        isDelete = true;
        if (owner.uLvl.getAttackType()==1) Unit.addPerSpeed -= 12;
        else Unit.addPerSpeed -= 3;
        if (Game.secondTick) {
            u.dealsMagicDamage(3, owner);
            if ((u.status & Unit.MAG_IMMORTAL)==0)
                ((flyText) Game.postEffects.getEffect(flyText.class)).Init(u, Color.PURPLE, "3", u.visGRP);
        }
    }
}
