package art.soft.spells;

import art.soft.triggers.gameTimer;
import art.soft.units.Unit;
import static art.soft.units.Unit.IS_ALIVE;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class Effect {

    public final static int BUFF_EFFECT = 1;
    public final static int UNIC_EFFECT = 2;
    public final static int EFFECT_MODIF_1 = 4;
    public final static int EFFECT_MODIF_2 = 8;
    public final static int SHOW_CHARGES = 0x10;
    public final static int DONT_SHOW_EFF = 0x20;
    public final static int BREAK_SPELL = 0x40;
    public final static int EFFECT_ONPOOL = 0x40000000;
    public final static int EFFECT_ONSTACK = 0x80000000;

    public final static int EFFECT_MASK_REMOVE_FROM_STACK = ~(EFFECT_ONPOOL | EFFECT_ONSTACK);

        // Пул "удалёных" эфектов
    private static Effect buffEffects;//, lastBuffered;

    public boolean isDelete;
    
    public String icon;
    public Image Icon;
    public int level, attrib = 3, modif = -1, statModif, manacost, cooldown;
        
        // Модификаторы способностей (цифры - номера битов) [modif]:
        // 1 - все сапоги
        // 2 - предметы на основе yasha


        // Модификаторы атаки (цифры - номера битов) [modif]:
        // 1 - модификаторы типа вампиризм [орб]
        // 2 - модификаторы стакающиеся с вампиризмом (скади) [бафф]
        // 1, 2 - уникальные модификиторы (дезолятор, диффуза)
        // 3 - модификаторы типа криты
        //      (перекрывают срабатывание других модификаторов такого типа)
        // 4 - различные блоки урона
        // 5 - предметы на основе sange
    
    public long cTimeEnd;
    public Unit owner;
    public Spell spell;

    public Effect next;
    
    public Effect copy() {
        Effect e = getEffect(this.getClass());
        return copy(e);
    }

    public Effect copy(Effect e) {
        e.statModif = statModif;
        e.cooldown = cooldown;
        e.cTimeEnd = cTimeEnd;
        e.manacost = manacost;
        e.spell = spell;
        //e.cooldown = cooldown;
        e.attrib = attrib;
        e.Icon = Icon;
        e.modif = modif;
        e.level = level;
        e.owner = owner;
        return e;
    }

    public void Load(){
        if (icon!=null) {
            Icon = Image.loadImage(icon);
            icon = null;
        }
    }

    public void setOwner(Unit u) {
        owner = u;
    }

    public void setSpell(Spell s) {
        spell = s;
    }

    public Spell getSpell() {
        return spell;
    }

    public Image getIcon(){
        return Icon;
    }

    public void doEffectsStats(Unit u, boolean stack){}

    public void doEffectsDamage(Unit u, boolean stack){}

    public int startAttackThis(Unit u){
        return 0;
    }

    public int unitAttackThis(Unit u){
        return 0;
    }

    public int startDamageAim(Unit u, Unit aim){
        return 0;
    }

    public int attackDamageAim(Unit u, Unit aim){
        return 0;
    }

    public int dealsClearDamage(Unit aim){
        return 0;
    }

    public void createModif(Unit u, Unit aim){}

    public Effect get2Modificator(){
        return this;
    }

    public void castSpellToPos(int x, int y) {}

    public void doAttackModificator(Unit u, int damage, int dmg){}

    public void giveItem(Unit reciver, Unit item, int slot) {}

    public void dropItem(Unit owner, Unit item, int slot) {}

    public void useItemAlready(Unit owner, Unit item, int slot) {}

    public void useItemOnUnit(Unit owner, Unit item, int slot, Unit aim) {}

    public void useItemToPos(Unit owner, Unit item, int slot, int x, int y) {}
 
    public void setLevel(int lvl){
        level = lvl;
    }

    public void incLevel(){
        setLevel(level + 1);
    }

    public boolean castSpell() {
        if (decMana()) {
            startCooldown();
            if ((spell.attrib & Spell.IS_CAST_SPELL)!=0) {
                owner.setLastSpell(spell);
                //castSpell(s, owner);
            }
            spell.isCast = false;
            return true;
        }
        return false;
    }

    public boolean castItem(Unit item) {
        if (decMana()) {
            startCooldown();
            owner.setCommonCooldown(item.item);
            return true;
        }
        return false;
    }

    public boolean haveMana(){
        if (owner!=null) return owner.uLvl.mp>=manacost;
        return false;
    }

    public boolean decMana(){
        if (owner!=null) {
            float mp = owner.uLvl.mp;
            if (mp>=manacost) {
                owner.uLvl.mp = mp - manacost;
                return true;
            }
        }
        return false;
    }

    public void resetCooldown() {
        cTimeEnd = 0;
    }

    public void decCooldown(){
        if (cTimeEnd - gameTimer.gameTime<=0) cTimeEnd = 0;
    }

    public int getCooldown(){
        return cTimeEnd==0 ? 0 : (int) (cTimeEnd - gameTimer.gameTime);
    }

    public boolean isNotCooldown() {
        return cTimeEnd==0;
    }

    public int getCooldownTime(){
        return cooldown;
    }

    public void setCooldownTime(int cTime){
        cooldown = cTime;
    }

    public void startCooldown(){
        cTimeEnd = gameTimer.gameTime + cooldown;
    }

    public void startCooldown(int cooldown){
        cTimeEnd = gameTimer.gameTime + cooldown;
    }

    public void addEffect(Unit u){
        isDelete = false;
        if ((attrib & EFFECT_ONSTACK)==0) {
            attrib |= EFFECT_ONSTACK;
            /*if (u.effects==null) {
                u.effects = u.lastEffect = this;
            } else {
                u.lastEffect.next = this;
                u.lastEffect = this;
            }
            next = null;*/
            next = u.effects;
            u.effects = this;
        }
    }

    public static Effect getEffect (Class<?> type) {
        Effect e = buffEffects;
        Effect p = null;
        while (e!=null) {
            if (type.isInstance(e)) {
                //if (e==lastBuffered) lastBuffered = p;
                if (p==null) buffEffects = e.next;
                else p.next = e.next;
                e.next = null;
                e.attrib &= ~ EFFECT_ONPOOL;
                return e;
            }
            p = e;
            e = e.next;
        }
        try {
            e = (Effect) type.newInstance();
        }catch( InstantiationException ex) {} catch (IllegalAccessException ex) {}
        return e;
    }

    public static Effect addEffect (Unit u, Unit owner, Effect ef) {
        Effect e = getEffect(ef.getClass());
        ef.copy(e).setOwner(owner);
        if (u!=null) e.addEffect(u);
        return e;
    }

    public static void poolEffect(Effect e) {
        if (e!=null && (e.attrib & EFFECT_ONPOOL)==0) {
            e.attrib |= EFFECT_ONPOOL;
            e.setOwner(null);
            /*if (buffEffects==null) {
                lastBuffered = buffEffects = e;
            } else {
                lastBuffered.next = e;
                lastBuffered = e;
            }
            e.next = null;*/
            e.next = buffEffects;
            buffEffects = e;
        }
    }

    public static void refreshStats(Unit u) {
        Effect e = u.effects;
        Effect pred = null;
        Effect ne;
        int mod = 0;
        while (e!=null) {
            ne = e.next;
            if (e.isNotCooldown() && e.isDelete) {
                //if (ne==null) u.lastEffect = pred;
                if (pred==null) u.effects = ne;
                else pred.next = ne;
                e.next = null;
                e.attrib &= ~ EFFECT_ONSTACK;
                if (u!=e.owner) poolEffect(e);
            } else {
                if ((u.status & IS_ALIVE)!=0) e.doEffectsStats(u, (mod & e.statModif)==0);
                mod |= e.statModif;
                e.decCooldown();
                pred = e;
            }
            e = ne;
        }
    }

    public static void refreshDamage(Unit u) {
        Effect e = u.effects;
        int mod = 0;
        while (e!=null) {
            e.doEffectsDamage(u, (mod & e.statModif)==0);
            mod |= e.statModif;
            e = e.next;
        }
    }

    public Effect addUnicUnitEffect(Unit u) {
        if ((attrib & BREAK_SPELL)!=0) u.status |= Unit.BREAK_SPELL;
        Class<?> type = getClass();
        Effect ef = u.effects;
        Effect e = null;
        while (ef!=null) {
            if (type.isInstance(ef)) {
                if (ef.owner==u && ef.level<level) {
                    ef.isDelete = true;
                } else {
                    e = ef; ef = ef.next; break;
                }
            }
            ef = ef.next;
        }
        while (ef!=null) {
            if (type.isInstance(ef)) ef.isDelete = true;
            ef = ef.next;
        }
        if (e!=null) {
            if (e.level>=level) {
                e.isDelete = false;
            } else {
                e.isDelete = false;
                e = copy(e);
            }
        } else e = addEffect(u, owner, this);
        return e;
    }

    public boolean copyUnicUnitEffect(Unit u){
        if ((attrib & BREAK_SPELL)!=0) u.status |= Unit.BREAK_SPELL;
        Class<?> type = getClass();
        Effect ef = u.effects;
        Effect e = null;
        boolean copy = false;
        while (ef!=null) {
            if (type.isInstance(ef)) {
                if (ef.owner==u && ef.level<level) {
                    ef.isDelete = true;
                } else {
                    e = ef; ef = ef.next; break;
                }
            }
            ef = ef.next;
        }
        while (ef!=null) {
            if (type.isInstance(ef)) ef.isDelete = true;
            ef = ef.next;
        }
        if (e!=null) {
            copy = true;
            if (e.level>=level) {
                e.isDelete = false;
            } else {
                e.isDelete = false;
                copy(e);
            }
            e.startCooldown();
        } else {
            addEffect(u);
            startCooldown();
        }
        return copy;
    }

    public void dispose(){
        if (Icon!=null) Icon.dispose();
    }
}
