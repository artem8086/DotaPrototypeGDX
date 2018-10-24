package maps.main.items;

import art.soft.Game;
import art.soft.specal.flyText;
import art.soft.spells.Effect;
import art.soft.units.Unit;
import art.soft.utils.selectors.SelectUnits;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Artem
 */
public class ArcaneBoots extends Effect {

    @Override
    public void doEffectsStats(Unit u, boolean stack) {
        u.uLvl.max_mp += 250;
        if (stack) Unit.add_speed += 60;
    }

    @Override
    public void useItemAlready(Unit owner, Unit item, int slot) {
        Unit u = SelectUnits.setCircle(owner, spell.influents, spell.radiusS);
        while (u!=null) {
            if (u.uLvl.max_mp>0) {
                u.uLvl.addMP(135);
                ((flyText) Game.postEffects.getEffect(flyText.class)).Init(u, Color.BLUE, "+135", u.visGRP);
            }
            u = SelectUnits.getNextUnit();
        }
    }
}
