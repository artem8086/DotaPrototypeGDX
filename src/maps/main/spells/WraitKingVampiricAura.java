package maps.main.spells;

import art.soft.Game;
import art.soft.specal.flyText;
import art.soft.spells.Effect;
import art.soft.units.Unit;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Артем
 */
public class WraitKingVampiricAura extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean modif){
        isDelete = true;
    }

    @Override
    public void doAttackModificator(Unit aim, int damage, int dmg){
        int h = owner.uLvl.HPreg((10 * level * damage) / 100);
        if (h>0) ((flyText) Game.postEffects.getEffect(flyText.class)).Init( owner, Color.GREEN, "+"+h, owner.visGRP | aim.visGRP );    
    }
}
