package art.soft.units;

import art.soft.Game;
import art.soft.utils.StackObj;

/**
 *
 * @author Артем
 */
public class ULevelCreep extends StackObj {

    public static int MIN_ARMOR_COUNT = -20;
    
    public static float HPpercent, MPpercent;
    
    public UnitData uData;

    public int uType;
    public float hp, mp;
    public int max_hp, max_mp;
    
    public float hpReg, mpReg;
    
    public float s_am, t_am; // основная и дополнительная броня
    public float magResist; // магическое сопротивление
    
    public int level, old_level;
    public int radView;

    public int add_dmg, add_AS;
    public int autoAttackRad;
    public Attack[] attacks;
    public Attack cur_Attack, nextAttack;
    public int curAttack;

    public ULevelCreep() {}

    public ULevelCreep(UnitData ud) {
        setData(ud);
    }
    
    public final ULevelCreep setData(UnitData ud) {
        uData = ud;

        if( ud.attacks!=null ){

            autoAttackRad = ud.auto_attack_radius * ud.auto_attack_radius;
            int len = ud.attacks.length;
            attacks = new Attack[len];
            for( len--; len>=0; len-- ){
                Attack a = ud.attacks[len];
                attacks[len] = a.copy();
            }
            cur_Attack = attacks[0];
        }
        return this;
    }

    public void reset() {
        level = 1;
        uType = uData.ID;

        s_am = uData.s_armor;
        magResist = 1f - uData.mag_resist;
        t_am = old_level = add_dmg = add_AS = 0;
        
        radView = uData.viewRadius;
        
        refreshMainAttrib();
        hp = max_hp;
        mp = max_mp;
    }

    public Attack incAttack() {
        if (nextAttack!=null) {
            cur_Attack = nextAttack;
            nextAttack = null;
        } else {
            curAttack ++;
            if( curAttack>=attacks.length ) curAttack = 0;
            cur_Attack = attacks[curAttack];
        }
        return cur_Attack;
    }

    public void setAim(Unit aim) {
        if (attacks!=null)
        for (Attack a : attacks) {
            a.aim = aim;
        }
    }

    public void clearAttacks() {
        if (attacks!=null)
        for (Attack a : attacks) {
            a.aim = null;
        }
    }

    public int dealsPhythDamage(int dmg) {
        float am = (int) (s_am+t_am);
        if( am<MIN_ARMOR_COUNT ) am = MIN_ARMOR_COUNT;
        float sub;
        if( am>=0 ) sub = (am*0.06f)/(1f+am*0.06f);
        else sub = (float) Math.pow(0.94f,-am)-1f;
        dmg = (int) (dmg * (1f-sub));
        return dmg;
    }

    public int dealsMagicDamage(int dmg) {
        return (int) (dmg * magResist);
    }

    public void setLevel(int lvl) {
        if (lvl>uData.max_level) level = uData.max_level;
        else if (lvl<=0) level = 0;
        else level = lvl;
    }

    public boolean incLevel() {
        level ++;
        if (level>=uData.max_level) {
            level = uData.max_level;
            return false;
        }
        return true;
        //refreshMainAttrib();
        //levelUp();
    }

    public void resetAttrib() {
        if (max_hp!=0) HPpercent = hp / max_hp; else HPpercent = 0;
        if (max_mp!=0) MPpercent = mp / max_mp; else MPpercent = 0;

        radView = uData.viewRadius;
        magResist = 1f - uData.mag_resist;

        s_am = t_am = hpReg = mpReg = add_dmg = add_AS = 0;
        max_hp = max_mp = 0;
    }

    public void addMagicResistance(float mag) {
        magResist *= 1f - mag;
    }

    public void refreshAttrib(Unit u) {
        hp += hpReg * Game.gCYCo;
        int hm = (int) hp;
        if( hm>max_hp ) hp = max_hp; else
        if( hm<=0 ) u.unitDieFrom( null );
        
        mp += mpReg * Game.gCYCo;
        hm = (int) mp;
        if( hm>max_mp ) mp = max_mp; else
        if( hm<0 ) mp = 0;
    }

    public void addHP(int h) {
        hp += h;
        if (hp>max_hp) hp = max_hp;
    }

    public void addMP(int m) {
        mp += m;
        if (mp>max_mp) mp = max_mp;
    }

    public int HPreg(int inc) {
        hp += inc;
        if( hp>max_hp ) {
            inc = max_hp - (int) hp;
            hp = max_hp;
        }
        return inc;
    }

    public int MPreg(int inc) {
        mp += inc;
        if( mp>max_mp ) {
            inc = max_hp - (int) hp;
            mp = max_mp;
        }
        return inc;
    }

    public void incSkill(int s) {}

    public void setStrenth() {}
    public void setAgility() {}
    public void setIntelect() {}
    public void setDamage() {
        if( attacks!=null ) for( Attack a : attacks )
            a.setDamage( uData.start_dmg + (int) (uData.inc_m_dmg * level), add_dmg );
    }

    public void addTDamage(int t_dmg) {
        if( attacks!=null ) for( Attack a : attacks )
            a.t_dmg += t_dmg;
    }

    public Attack getCurAttack() {
        return cur_Attack;
    }

    public int getMainDamage() {
        if (attacks!=null)
            return attacks[curAttack].m_dmg;
        else return 0;
    }

    public int getAttackType() {
        if (attacks!=null) {
            if (attacks[curAttack].rangeBul==null) return 1;
            else return 2;
        } else return 0;
    }

    public void refreshMainAttrib() {
        max_hp += uData.start_hp + (int) (uData.inc_hp_str * level);
        hp = max_hp * HPpercent;
        
        max_mp += uData.start_mp + (int) (uData.inc_mp_int * level);
        mp = max_mp * MPpercent;
        
        hpReg += (uData.start_hp_reg + (uData.inc_hp_reg * level));
        mpReg += (uData.start_mana_reg + (uData.inc_mana_reg * level));
        setDamage();
        setAttackSpeed((int) (level * uData.inc_agl_AS) + add_AS);
        s_am += uData.s_armor + level * uData.p_armor;
    }

    public void setAttackSpeed(int ias) {
        if( attacks!=null ) for( Attack a : attacks )
            a.setAttackSpeed(ias);
    }

    public void setHP(float h) {
        hp = h;
    }
    
    public int getCurHP() {
        return (int) hp;
    }

    public int getSkill() {
        return 0;
    }
    
    public int getSkillPool() {
        return 0;
    }

    public int getOldHP() {
        return 0;
    }

    public void setMana(float m) {
        mp = m;
    }
    
    public int getCurMana() {
        return (int) mp;
    }
}
