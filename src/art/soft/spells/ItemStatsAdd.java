package art.soft.spells;

import art.soft.units.ULevelCreep;
import art.soft.units.ULevelHero;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class ItemStatsAdd extends Effect {

    public int dmg, hp, mp, mSpeed, ms_per, armor, aSpeed;
    public float t_agl, t_str, t_int, hp_reg, mp_reg, hp_per, mp_per, magResist;
    
    @Override
    public Effect copy(Effect e){
        ItemStatsAdd is = (ItemStatsAdd) super.copy(e);
        is.dmg = dmg;
        is.hp = hp;
        is.mp = mp;
        is.mSpeed = mSpeed;
        is.mp_per = mp_per;
        is.hp_per = hp_per;
        is.ms_per = ms_per;
        is.armor = armor;
        is.aSpeed = aSpeed;
        is.t_agl = t_agl;
        is.t_str = t_str;
        is.t_int = t_int;
        is.hp_reg = hp_reg;
        is.mp_reg = mp_reg;
        is.magResist = magResist;
        return is;
    }

    @Override
    public void doEffectsStats(Unit u, boolean stack) {
        ULevelCreep uc = u.uLvl;
        if (stack) {
            Unit.add_speed += mSpeed;
            Unit.addPerSpeed += ms_per;
        }
        Unit.addPerHPReg += hp_per;
        Unit.addPerMPReg += mp_per;
        uc.add_dmg += dmg;
        uc.add_AS += aSpeed;
        uc.max_hp += hp;
        uc.max_mp += mp;
        uc.hpReg += hp_reg;
        uc.mpReg += mp_reg;
        uc.t_am += armor;
        uc.addMagicResistance(magResist);
        if (uc instanceof ULevelHero) {
            ULevelHero ul = (ULevelHero) uc;
            ul.t_str += t_str;
            ul.t_agl += t_agl;
            ul.t_int += t_int;
        }
    }
}
