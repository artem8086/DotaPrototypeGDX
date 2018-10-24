package art.soft.units;

import art.soft.Game;
import art.soft.gui.Language;
import static art.soft.multiplayer.ClientServer.random;
import art.soft.specal.*;
import art.soft.spells.Damage;
import static art.soft.spells.Damage.DONT_BREAK_SPELL;
import static art.soft.spells.Damage.TYPE_MISS;
import static art.soft.spells.Damage.TYPE_PHYTH;
import art.soft.spells.Effect;
import static art.soft.spells.Effect.EFFECT_MODIF_1;
import static art.soft.spells.Effect.EFFECT_MODIF_2;
import art.soft.spells.Spell;
import static art.soft.units.Unit.IS_ALIVE;
import static art.soft.units.Unit.IS_SILENCE;
import static art.soft.units.Unit.PYS_IMMORTAL;
import static art.soft.units.Unit.TRUE_STRIKE;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Артем
 */
public class Attack {

    public static int type, mDmg, tDmg;
    public static Effect e, e2, ne, n2e;

    public AnimData rangeBul, rangeBul2;
    public String bulDir; // тип атаки: null - ближня, иначе данные о снаряде
    public float misSpeed, halfATime, attackTime;
                // misSpeed - скорость снаряда
                // halfATime - время после которого происходит удар

    public boolean modifOn = true;
    public int m_dmg, t_dmg; // основной и второстепенный дамаг
    public int startAS, autoDist, attack_anim, numAttack = 1;

    public Damage dmg;

    public Unit aim;
    public int radius;

    public Attack copy(){
        Attack a = new Attack();
        a.rangeBul = rangeBul;
        a.autoDist = autoDist;
        a.startAS = startAS;
        a.m_dmg = m_dmg;
        a.t_dmg = t_dmg;
        a.modifOn = modifOn;
        a.halfATime = halfATime;
        a.misSpeed = misSpeed;
        a.attackTime = attackTime;
        a.attack_anim = attack_anim;
        a.radius = radius;
        return a;
    }

    public void Load(){
        if (bulDir!=null) {
            rangeBul = AnimData.load("bullets/"+bulDir );
            bulDir = null;
        }
    }

    public void Init(){
        radius *= radius;
        t_dmg = 0;
        setAttackSpeed(startAS);
    }

    public void setDamage( int dmg, int add_dmg ){
        m_dmg = dmg;
        t_dmg = add_dmg;
    }

    public void setAttackSpeed( int ias ){
        ias += startAS;
        if (ias > Game.map.MAX_ATTACK_SPEED) ias = Game.map.MAX_ATTACK_SPEED; else
        if (ias < Game.map.MIN_ATTACK_SPEED) ias = Game.map.MIN_ATTACK_SPEED;
        attackTime = 100f / (100 + ias);
    }

    public void dealsDamageToAims( Unit u, Unit aim ){
        Attack a = this;

        if (aim!=null) {
            if (u.owners.groupInGroup(aim.isVisible) && (aim.status & IS_ALIVE)!=0 && (aim.status & PYS_IMMORTAL)==0) {
                int modif = 0x80000000;
                tDmg = 0;
                mDmg = dmg.dmg;
                type = dmg.level;
                Spell spells[] = u.spells;
                Effect ef;
                if (spells!=null && (u.status & IS_SILENCE)==0)
                for (Spell s : spells) if (s!=null) {
                    ef = s.getCastEffect();
                    if (ef!=null && ef.level>0 && (ef.modif & modif)==0) {
                        modif |= ef.attackDamageAim(u, aim);
                        //addEffect(ef, u, aim);
                        ef.castSpell();
                    }
                    s.isCast = false;
                }
                if (modifOn) {
                    ef = u.effects;
                    while (ef!=null) {
                        if (ef.level>0 && (ef.modif & modif)==0) {
                            modif |= ef.attackDamageAim(u, aim);
                            //addEffect(ef, u, aim);
                        }
                        ef = ef.next;
                    }
                }
                mDmg = dmg.dmg = mDmg + tDmg;
                dmg.level = type;
                tDmg = 0;
                modif = 0x80000000;
                ef = aim.effects;
                while (ef!=null) {
                    if (ef.level>0 && (ef.modif & modif)==0)
                        modif |= ef.unitAttackThis(u);
                    ef = ef.next;
                }
                dmg.dmg = mDmg + tDmg;
                dmg.level = type;
                if ((u.status & TRUE_STRIKE)==0 && (type & TYPE_MISS)!=0 && modifOn) {
                    poolsModificators(dmg.effects);
                    poolsModificators(dmg.next);
                    Effect.poolEffect(dmg);
                    dmg.effects = dmg.next = null;
                    ef = null;
                    Game.postEffects.getEffect(flyText.class).Init( u, Color.RED, Language.words.miss, u.visGRP | aim.visGRP );
                } else ef = dmg;
                if (rangeBul2!=null) {
                    Bullet.addBullet( u, rangeBul2, aim, (Damage) ef, misSpeed, u.visGRP | aim.visGRP );
                } else if (ef!=null) {
                        if ((ef.level & DONT_BREAK_SPELL)==0) aim.status |= Unit.BREAK_SPELL;
                        dmg.setDamege(aim);
                    }
                    //if( (aim.status & IS_ALIVE)==0 ) u.stand();
            } else
                if (aim==this.aim) this.aim = null;
        }
        rangeBul2 = rangeBul;
    }

    public void startDealsDamage(Unit u){
        dmg = (Damage) Effect.getEffect(Damage.class);
        dmg.setOwner(u);
        n2e = ne = e2 = e = null;
        rangeBul2 = rangeBul;
        if (aim!=null) {
            mDmg = t_dmg + m_dmg;
            type = TYPE_PHYTH;
            int modif = 0x80000000;
            tDmg = 0;
            Spell spells[] = u.spells;
            Effect ef;
            if (spells!=null && (u.status & IS_SILENCE)==0)
            for (Spell s : spells) if (s!=null) {
                ef = s.getCastEffect();
                if (ef!=null && ef.level>0 && (ef.modif & modif)==0) {
                    modif |= ef.startDamageAim(u, aim);
                    addEffect(ef, u, aim);
                }
            }
            if (modifOn) {
                ef = u.effects;
                while (ef!=null) {
                    if (ef.level>0 && (ef.modif & modif)==0) {
                        modif |= ef.startDamageAim(u, aim);
                        addEffect(ef, u, aim);
                    }
                    ef = ef.next;
                }
            }
            dmg.dmg = mDmg + tDmg;
            dmg.level = type;
            if (rangeBul2!=null)
            if ((aim.getZ()-u.getZ())>Unit.MAX_HEIGHT_TO_GO) {
                if (random.nextBoolean(25)) type |= TYPE_MISS;
            }
            dmg.next = e;
            dmg.effects = e2;

            Attack a = this;

            if (aim!=null && u.owners.groupInGroup(aim.isVisible) && (aim.status & IS_ALIVE)!=0 && (aim.status & PYS_IMMORTAL)==0) {
                Unit aim = a.aim;
                Damage next = null;
                modif = 0x80000000;
                ef = aim.effects;
                while (ef!=null) {
                    if (ef.level>0 && (ef.modif & modif)==0)
                        modif |= ef.startAttackThis(u);
                    ef = ef.next;
                }
            }
        }
        n2e = ne = e2 = e = null;
    }

    public static void setModificators(Effect e, Unit aim){
        while (e!=null) {
            Effect n = e.next;
            if ((e.attrib & Effect.UNIC_EFFECT)!=0) {
                if (e.copyUnicUnitEffect(aim)) {
                    Effect.poolEffect(e);
                }
            } else e.addEffect(aim);
            e = n;
        }
    }

    public static void poolsModificators(Effect e){
        Effect n;
        while (e!=null) {
            n = e.next;
            Effect.poolEffect(e);
            e = n;
        }
    }

    private static void addEffect(Effect ef, Unit owner, Unit aim){
        if (ef.haveMana() && ef.isNotCooldown()) {
            int a = ef.attrib;
            Effect eff;
            if ((a & EFFECT_MODIF_1)!=0) {
                eff = Effect.addEffect(null, owner, ef);
                eff.createModif(owner, aim);
                if (e2==null) e2 = n2e = eff;
                else {
                    n2e.next = eff;
                    n2e = eff;
                }
            }
            if ((a & EFFECT_MODIF_2)!=0) {
                eff = ef.get2Modificator();
                if (eff!=null) {
                    eff = Effect.addEffect(null, owner, eff);
                    eff.setLevel(ef.level);
                    eff.createModif(owner, aim);
                    eff.startCooldown();
                    if (e==null) e = ne = eff;
                    else {
                        ne.next = eff;
                        ne = eff;
                    }
                }
            }
        }
    }

    public void dispose(){
        if (rangeBul!=null) rangeBul.dispose();
    }
}
