package maps.main.spells;

import art.soft.spells.Effect;
import art.soft.units.Unit;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class rikiPermanentInvisibility extends Effect {

    public Image tIcon;
    public int cType;

    @Override
    public Effect copy(Effect e){
        rikiPermanentInvisibility ef = (rikiPermanentInvisibility) super.copy(e);
        ef.tIcon = e.Icon;
        return ef;
    }

    @Override
    public Image getIcon(){
        if (Icon!=null) return Icon;
        else return tIcon;
    }

    @Override
    public void doEffectsStats(Unit u, boolean modif){
        if (level>0) {
            if (isNotCooldown()) {
                u.uLvl.hpReg += 10 * level;
                u.inInvis = -1;
                u.color = Unit.invisColor;
                Icon = tIcon;
            } else Icon = null;
        }
    }

    @Override
    public void setLevel(int lvl){
        setCooldownTime((5 - lvl) * 2000);
        level = lvl;
    }

    @Override
    public int startDamageAim(Unit u, Unit aim){
        if (level>0) startCooldown();
        return 0;
    }
}
