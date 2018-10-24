package art.soft.spells;

import static art.soft.spells.Effect.EFFECT_MASK_REMOVE_FROM_STACK;
import art.soft.units.Attack;
import art.soft.units.Unit;
import static art.soft.units.Unit.IS_SILENCE;
import art.soft.utils.Stack;
import art.soft.utils.StackObj;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class Spell extends StackObj {

    private static final Stack poolSpells = new Stack();
    public static final int PASSIVE_MASK_SPELL = 0xFFBF;

    public static final int SPELL_CAST_NOW = 0x80000000;

    public static final int PASSIVE_SPELL   = 0x0001;
    public static final int UNIT_AURA_ON    = 0x0002;
    public static final int UNIT_AURA_OFF   = 0x0004;
    public static final int AUTO_SPELL_CAST = 0x0008;
    public static final int ATTACK_MODIF    = 0x0010;
    public static final int CAST_ON_UNIT    = 0x0020;
    public static final int IS_ACTIVE_SPELL = 0x0040;
    public static final int IS_CAST_SPELL   = 0x0080;
    public static final int CAST_ON_AOE     = 0x0100;
    public static final int CAST_ALREADY    = 0x0200;
    public static final int THIS_IS_SHOP    = 0x0400;
    public static final int AURA_DISABLED   = 0x0800;

    public String icon, Name;
    public Image Icon;
    public Attack attack;
    public int attrib, number = 1, nextUpgrade = 1, incUpgrade = 2, maxUpgrade = 4;

    public boolean isAutoCastOn = false, isCast = false;

    public Effect effect;

    public static final int YOUR_AURA           = 0x0001;
    public static final int CREEPS_AURA         = 0x0002;
    public static final int HERO_AURA           = 0x0004;
    public static final int TOWERS_AURA         = 0x0008;
    public static final int ALIES_AURA          = 0x0010;
    public static final int ENEMY_AURA          = 0x0020;
    public static final int RANGE_UNITS_AURA    = 0x0040;
    public static final int MEELE_UNITS_AURA    = 0x0080;
    public static final int ITEMS_UNITS_AURA    = 0x0100;

    public Effect auraEffect;
        // если эффект ауры не указын, а в спеле стоит флажок что это аура,
        // значит этот спел указывает что это лавка,
        // где ID лавки будет хранится в number

    public int radius, radiusAOE, radiusS, influents;

    public Spell copy(){
        Spell s = poolSpells.getFirst(Spell.class);
        s.Icon = Icon;
        Effect e = effect;
        if (e!=null) s.effect = e.copy();
        e = auraEffect;
        if (e!=null) s.auraEffect = e.copy();
        if (attack!=null) {
            s.attack = attack.copy();
        }
        s.Name = Name;
        s.attrib = attrib;
        s.number = number;
        s.nextUpgrade = nextUpgrade;
        s.incUpgrade = incUpgrade;
        s.maxUpgrade = maxUpgrade;
        s.radius = radius;
        s.radiusS = radiusS;
        s.radiusAOE = radiusAOE;
        s.influents = influents;
        return s;
    }

    public void preLoad(){
        if (effect!=null) effect.Load();
        if (auraEffect!=null) auraEffect.Load();
        if (icon!=null) {
            Icon = Image.loadImage(icon);
            icon = null;
        }
        if (attack!=null) attack.Init();
        if (radius>0) radiusS = radius * radius;
        else radiusS = radius;
    }

    public void postLoad(){
        if (attack!=null) attack.Load();
    }

    public Image getIcon(){
        if (Icon==null) {
            if (effect!=null ) {
                Image i = effect.getIcon();
                if (i!=null) return i;
            }
            if (auraEffect!=null) return auraEffect.getIcon();
        }
        return Icon;
    }

    public Effect getCastEffect(){
        if (isCast || isAutoCastOn) {
            if (effect!=null && effect.isNotCooldown() && (effect.owner.status & IS_SILENCE)==0) return effect;
        }
        return null;
    }

    public Attack getCastAttack(){
        if (effect!=null && attack!=null) {
            if (isAutoCastOn || isCast)
            if (effect.isNotCooldown() && effect.haveMana()) return attack;
        }
        return null;
    }

    public int getManacost(){
        if (effect!=null ) return effect.manacost;
        return 0;
    }

    public int getCooldown(){
        if (effect!=null ) return effect.getCooldown();
        if (auraEffect!=null) return auraEffect.getCooldown();
        return 0;
    }

    public int getCooldownTime(){
        if (effect!=null ) return effect.getCooldownTime();
        if (auraEffect!=null) return auraEffect.getCooldownTime();
        return 0;
    }

    public void setCooldouwnTimeEnd(Spell s) {
        if (effect!=null && s.effect!=null) {
            if (effect.cTimeEnd > s.effect.cTimeEnd)
                s.effect.cTimeEnd = effect.cTimeEnd;
            else effect.cTimeEnd = s.effect.cTimeEnd;
        }
        if (auraEffect!=null && s.auraEffect!=null) {
            if (auraEffect.cTimeEnd > s.auraEffect.cTimeEnd)
                s.auraEffect.cTimeEnd = auraEffect.cTimeEnd;
            else auraEffect.cTimeEnd = s.auraEffect.cTimeEnd;
        }
    }

    public void addAura(Unit u) {
        next = u.aurs;
        u.aurs = this;
    }

    public static void clearAurs(Unit u){
        /*Spell a = u.aurs;
        Spell n;
        while (a!=null) {
            n = (Spell) a.next;
            a.next = null;
            a = n;
        }*/
        u.aurs = null;
    }

    public void incSpellLevel(Unit u){
        if (maxUpgrade>0) {
            int lvl = u.uLvl.level;
            if (lvl-u.uLvl.old_level>0 && lvl>=nextUpgrade) {
                if (effect!=null) effect.incLevel();
                if (auraEffect!=null) auraEffect.incLevel();
                nextUpgrade += incUpgrade;
                maxUpgrade --;
                u.uLvl.old_level ++;
            }
        }
    }

    public Effect getEffect() {
        if (effect!=null) {
            return effect;
        } else if (auraEffect!=null) {
            return auraEffect;
        } else return null;
    }

    public int canUpgradeSpell(Unit u){
        if (maxUpgrade<=0) return 0;
        else {
            int l = u.uLvl.level;
            int d = l - u.uLvl.old_level;
            int lvl = l - nextUpgrade;
            if (lvl<=0) lvl = 0;
            else lvl /= incUpgrade;
            if (l>=nextUpgrade) lvl ++;
            if (d<lvl) lvl = d;
            return lvl;
        }
    }
    
    public int getSpellLevel(){
        if (effect!=null) {
            return effect.level;
        } else if (auraEffect!=null) {
            return auraEffect.level;
        } else return 0;
    }

    public Unit getSpellOwner(){
        if (effect!=null) {
            return effect.owner;
        } else if (auraEffect!=null) {
            return auraEffect.owner;
        } else return null;
    }

    public void refreshSpell(){
        if (effect!=null) if ((effect.attrib & Effect.EFFECT_ONSTACK)==0) effect.decCooldown();
        if (auraEffect!=null) if ((auraEffect.attrib & Effect.EFFECT_ONSTACK)==0) auraEffect.decCooldown();
    }

    public Spell setSpell(Unit u){
        if (effect!=null) {
            effect.setOwner(u);
            effect.setSpell(this);
            if ((attrib & PASSIVE_SPELL)!=0) effect.addEffect(u);
        }
        if ((attrib & THIS_IS_SHOP)!=0) {
            if (influents!=0) addAura(u);
            Effect.poolEffect(auraEffect);
            auraEffect = null;
        } else
        if (auraEffect!=null) {
            auraEffect.setOwner(u);
            auraEffect.setSpell(this);
            if (influents!=0) addAura(u);
        }
        return this;
    }

    public void deleteSpell(Unit u) {
        Effect e = u.effects;
        Effect pred = null;
        Effect ne;
        while (e!=null) {
            ne = e.next;
            if (e.getSpell()==this) {
                //if (ne==null) u.lastEffect = pred;
                if (pred==null) u.effects = ne;
                else pred.next = ne;
                e.attrib &= EFFECT_MASK_REMOVE_FROM_STACK;
                e.next = null;
            } else
                pred = e;
            e = ne;
        }
        Spell a = u.aurs;
        Spell p = null;
        Spell na;
        while (a!=null) {
            na = (Spell) a.next;
            if (a==this) {
                if (p==null) u.aurs = na;
                else p.next = na;
                next = null;
                return;
            }
            p = a;
            a = na;
        }
    }

    @Override
    public void relese() {
        Icon = null;
        attack = null;
        icon = Name = null;
        Effect.poolEffect(effect);
        Effect.poolEffect(auraEffect);
        auraEffect = effect = null;
        poolSpells.add(this);
    }

    public void dispose(){
        if (Icon!=null) Icon.dispose();
        if (attack!=null) attack.dispose();
        if (effect!=null) effect.dispose();
        if (auraEffect!=null) auraEffect.dispose();
    }
}
