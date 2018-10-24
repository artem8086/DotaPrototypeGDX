package art.soft.units;

import art.soft.Game;
import art.soft.items.ItemData;
import static art.soft.items.Shop.START_UNIT_PRIORETET;
import art.soft.items.ShopItem;
import art.soft.multiplayer.*;
import art.soft.spells.Spell;
import static art.soft.units.Unit.DELETE_WD;
import static art.soft.units.Unit.IS_ALIVE;
import static art.soft.units.Unit.MINI_MAP_VIS;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class UnitData {

    /*public static final int UNIT_IS_DECOR = 0;
    public static final int UNIT_IS_EFFECT = 1;
    public static final int UNIT_IS_ART = 2;
    public static final int UNIT_IS_CREEP = 3;
    public static final int SIMPLE_BULDING = 4;
    public static final int UNIT_IS_HERO = 5;
    public static final int IMPROVING_BULDING = 6;*/

    public int ID;
    public String anim;
    public AnimData aData;
    public float zoom;
    public int unit_height;

    public String portrait, icon, miniIcon;
    public Image portraitIm, Icon, mIcon;

    public boolean isLoad, isHero;

    public float s_str, p_str; // начальное значение и прирост силы
    public float s_agl, p_agl; // начальное значение и прирост ловкости
    public float s_int, p_int; // начальное значение и прирост разума

    public float s_armor, p_armor; // начальное значение, прирост брони
    public float mag_resist; // начальное магическое сопротивление

    public int collisionType, collisionMask;
    public int unitStatus; // свойства юнита:
 
    public int radius, y_pos, body_height;
    public int viewRadius, nightVRadius;
    public int softRadius = 0; // радиус остановки при встрече юнита

    public int give_start_skill; // даёт стартовый опыта за смерть
    public int give_per_lvl_skill; // даёт опыт за каждый уровень смерть
    public int radius_skill_give; // радиус отдачи опыта

    public float inc_m_dmg; // прирост урона за еденицу основной характеристики
    public float inc_agl_AS; // прирост скорости атаки за еденицу ловкости

    public float inc_hp_reg; // прирост хп регена за еденицу силу
    public float inc_mana_reg; // прирост мана регена за еденицу силу

    public float inc_hp_str; // прирост хп за еденицу силу
    public float inc_mp_int; // прирост маны за еденицу разума

    public int main_atr; // главный атрибут: 0 - сила, 1 - ловкость, 2 - разум

    public int start_skill_pool;
    public int inc_skill_pool;

    public String name1, name2;

    public int max_level, start_speed;

    public float start_hp_reg; // начальный хп реген
    public float start_mana_reg; // начальный мана реген

    public int start_hp;
    public int start_mp;

    public int time_die = 15000;
    public int shopPrior = START_UNIT_PRIORETET; // приоритет доступа к магазину

    public float attackHP;
    public int auto_attack_radius;
    public int start_dmg; // начальное значение урона
    public Attack attacks[];


            // Данные анимации
    public int stand_anim;
    public int spec_stand_anim;
    public int move_anim;
    public int die_anim;

            // Данные анимаций спец-эффектов
    public int shadow_anim = 0;
    public int circle_anim = 1;

            // Данные скилов
    public Spell attribSpell;
    public Spell spells[];
    public int startS, numS;
    
            // Данные артефактов
    public int numItems, canBeTaken;
    public ItemData itemData;
    public ShopItem shopData;

    public static UnitData loadUnitData(String file, int id) {
        UnitData ud = Game.json.fromJson(UnitData.class, Game.openFile( file+".json" ) );
        ud.preLoad();
        ud.ID = id;
        return ud;
    }

    public void preLoad() {
        if (portrait!=null) {
            portraitIm = Image.loadImage(portrait);
            portrait = null;
        }
        if (miniIcon!=null) {
            mIcon = Image.loadImage(miniIcon);
            miniIcon = null;
        }
        if (icon!=null) {
            Icon = Image.loadImage(icon);
            icon = null;
        }
        unitStatus ^= IS_ALIVE | MINI_MAP_VIS | DELETE_WD;
        isLoad = false;
        softRadius *= softRadius;
        radius_skill_give *= radius_skill_give;
        if (attacks!=null)
        for (Attack a : attacks) a.Init();
        if (attribSpell!=null) attribSpell.preLoad();
        if (spells!=null)
        for (Spell s : spells) if (s!=null) s.preLoad();
    }
 
    public void Init() {
        if (itemData!=null) itemData.Init(this);
    }

    public void postLoad() {
        isLoad = true;
        aData = AnimData.load("anim/"+anim);
        anim = null;
        if (attacks!=null)
        for (Attack a : attacks) a.Load();
        if (attribSpell!=null) attribSpell.postLoad();
        if (spells!=null)
        for (Spell s : spells) if (s!=null) s.postLoad();
        if (itemData!=null) itemData.postLoad();
    }

    public Unit createUnit(PlayerGroup owners) {
        if (!isLoad) postLoad();
        Unit u = Game.poolUnit.getFirst(Unit.class);
        u.setData(this, collisionType, collisionMask);
        if (isHero)
            u.uLvl = Game.poolHero.getFirst(ULevelHero.class).setData(this);
        else
            u.uLvl = Game.poolCreep.getFirst(ULevelCreep.class).setData(this);
        u.startS = startS;
        u.numS = numS;
        if (attribSpell!=null) u.attribSpell = attribSpell.copy().setSpell(u);
        if (spells!=null) {
            int len = spells.length;
            Spell[] s = u.spells = new Spell[len];
            for (len--; len>=0; len--) if (spells[len]!=null) {
                s[len] = spells[len].copy().setSpell(u);
            }
        }
        if (itemData!=null) u.item = itemData.getItem();
        resetUnit(u, owners);
        return u;
    }

    public void resetUnit(Unit u, PlayerGroup owners) {
        u.uLvl.reset();
        u.status = unitStatus;
        u.canBeTaken = canBeTaken;
        //u.setPos(x, y);
        u.timeDie = - time_die;
        u.setAnim(0);
        u.setOwners(owners);
        u.yPos = y_pos;
        u.rVis = viewRadius * viewRadius;
    }

    public static UnitData getUnitByName(String name) {
        if (name!=null)
        for (int i=Game.uData.length-1; i>=0; i --) {
            UnitData ud = Game.uData[i];
            if (name.equals(ud.name2)) return ud;
        }
        return null;
    }

    public void dispose(){
        if (portraitIm!=null) portraitIm.dispose();
        if (mIcon!=null) mIcon.dispose();
        if (Icon!=null) Icon.dispose();
        if (aData!=null) aData.dispose();
        if (attacks!=null)
            for (Attack a : attacks) a.dispose();

        if (spells!=null)
            for (Spell s : spells) if (s!=null) s.dispose();
        
        if (itemData!=null) itemData.dispose();
    }
}
