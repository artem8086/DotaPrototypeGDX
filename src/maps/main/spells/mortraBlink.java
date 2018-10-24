package maps.main.spells;

import art.soft.Game;
import art.soft.units.Attack;
import art.soft.units.Unit;
import art.soft.utils.Image;
import art.soft.utils.Point;
import art.soft.spells.Damage;
import art.soft.spells.Effect;

/**
 *
 * @author Артем
 */
public class mortraBlink extends Effect {

    public Effect effect;

    @Override
    public void Load(){
        super.Load();
        effect.Load();
    }

    @Override
    public void setOwner(Unit u){
        if (effect!=null) effect.setOwner(u);
        super.setOwner(u);
    }

    @Override
    public Effect copy(Effect e){
        mortraBlink ef = (mortraBlink) super.copy(e);
        ef.effect = effect.copy();
        return ef;
    }

    @Override
    public void setLevel(int lvl){
        setCooldownTime((5 - lvl) * 5000);
        effect.setLevel(lvl);
        super.setLevel(lvl);
    }

    
    @Override
    public Image getIcon(){
        return effect.Icon;
    }

    @Override
    public int attackDamageAim(Unit u, Unit aim){
        Attack.type = Damage.TYPE_NONE;
        Point p = Game.map.teleportRayCast(u.px, u.py, aim.px, aim.py, u.cMask);
        u.teleport(p.x, p.y);
        ((mortraBlinkAS) effect).aim = aim;
        effect.startCooldown();
        effect.addEffect(u);
        if (aim.owners.groupInGroup(u.frendGRP)) u.setMoveToUnit(aim, true);
        else u.setAttackUnit(aim, true, true);
        return 0;
    }
}
