package art.soft.units;

import art.soft.Game;

/**
 *
 * @author Артем
 */
public class ULevelHero extends ULevelCreep {
    
    public float m_str, t_str; // основная и второстепнная сила
    public float m_agl, t_agl; // основная и второстепнная ловкость
    public float m_int, t_int; // основной и второстепнный разум
    
    public int skill, skill_pool;

    public float old_hp;

    public ULevelHero() {}

    public ULevelHero( UnitData ud ){
        super( ud );
    }

    @Override
    public void reset(){
        level = 1;
        uType = uData.ID;

        s_am = uData.s_armor;
        t_am = old_level = add_dmg = add_AS = 0;

        skill_pool = uData.start_skill_pool;
        
        skill = 0;
        
        m_str = m_agl = m_int = 0;
        t_str = t_agl = t_int = 0;

        refreshMainAttrib();
        hp = max_hp;
        mp = max_mp;
    }

    @Override
    public void setLevel(int lvl){
        super.setLevel(lvl);
        skill_pool = uData.start_skill_pool + uData.inc_skill_pool * level;
        skill = 0;
    }

    /*public void incLevel(){
        level ++;
        m_str += uData.p_str;
        m_agl += uData.p_agl;
        m_int += uData.p_int;
        refreshMainAttrib();
        setDamage();
        //levelUp();
    }*/
    
    @Override
    public void incSkill( int s ){
        skill += s;
        while (skill>skill_pool) {
            if (incLevel()) {
                skill -= skill_pool;
                skill_pool += uData.inc_skill_pool;
            } else {
                skill = skill_pool;
                break;
            }
        }
    }

    @Override
    public void setStrenth(){
        m_str += uData.s_str + uData.p_str * level;
        int str = (int) (m_str + t_str);
        max_hp += uData.start_hp + (int) (uData.inc_hp_str * str);
        hp = max_hp * HPpercent;
        hpReg += (uData.start_hp_reg + (uData.inc_hp_reg * str));
    }

    @Override
    public void setAgility(){
        m_agl += uData.s_agl + uData.p_agl * level;
        int agl = (int) (m_agl + t_agl);
        s_am += uData.s_armor + agl * uData.p_armor;
        setAttackSpeed((int) (agl * uData.inc_agl_AS) + add_AS);
    }

    @Override
    public void setIntelect(){
        m_int += uData.s_int + uData.p_int * level;
        int intl = (int) (m_int + t_int);
        max_mp += uData.start_mp + (int) (uData.inc_mp_int * intl);
        mp = max_mp * MPpercent;
        mpReg += (uData.start_mana_reg + (uData.inc_mana_reg * intl));
    }

    @Override
    public void setDamage(){
        if (attacks!=null) {
            int dmg = uData.start_dmg;
            if (uData.main_atr == 0) dmg += uData.inc_m_dmg * (m_str + t_str); else
            if (uData.main_atr == 1) dmg += uData.inc_m_dmg * (m_agl + t_agl);
            else dmg += uData.inc_m_dmg * (m_int + t_int);
            for (Attack a : attacks) a.setDamage(dmg, add_dmg);
        }
    }

    @Override
    public int getOldHP(){
        return (int) old_hp;
    }

    @Override
    public int getSkill(){
        return skill;
    }

    @Override
    public int getSkillPool(){
        return skill_pool;
    }

    @Override
    public void resetAttrib(){
        super.resetAttrib();
        m_str = m_agl = m_int = 0;
        t_str = t_agl = t_int = 0;
    }

    @Override
    public void refreshAttrib(Unit u) {
        super.refreshAttrib(u);
        if (old_hp>max_hp) old_hp = max_hp;
        else {
            if (hp>=old_hp) old_hp = hp;
            else old_hp -= max_hp * 0.6f * Game.gCYCo;
        }
    }
        
    @Override
    public void refreshMainAttrib() {
        setStrenth();
        setIntelect();
        setAgility();
        setDamage();
    }
}
