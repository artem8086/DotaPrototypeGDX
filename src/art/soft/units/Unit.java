package art.soft.units;

import art.soft.Game;
import static art.soft.Game.allUnits;
import art.soft.Graphics;
import art.soft.gui.infoWin;
import art.soft.items.Item;
import static art.soft.items.Item.REMOVE_ITEM;
import static art.soft.items.ItemData.ITEM_CANT_BE_DROPED;
import static art.soft.items.ItemData.ITEM_DROP_WHEN_DIE;
import static art.soft.items.ItemData.ITEM_IN_ONE_SLOT;
import art.soft.items.Shop;
import art.soft.map.GameMap;
import art.soft.multiplayer.ClientServer;
import art.soft.multiplayer.Player;
import art.soft.multiplayer.PlayerGroup;
import art.soft.specal.Bullet;
import art.soft.specal.specEffect;
import art.soft.spells.Effect;
import art.soft.spells.Spell;
import static art.soft.spells.Spell.YOUR_AURA;
import art.soft.utils.Image;
import com.badlogic.gdx.math.MathUtils;
import art.soft.units.actions.AttackAct;
import art.soft.units.actions.CastSpellToPos;
import art.soft.units.actions.DropItemToPos;
import art.soft.units.actions.DropItemToUnit;
import art.soft.units.actions.moveToPos;
import art.soft.units.actions.moveToUnit;
import art.soft.units.actions.useItemAlready;
import art.soft.units.actions.useItemOnUnit;
import art.soft.units.actions.useItemToPos;
import art.soft.utils.StackObj;

/**
 *
 * @author Артем
 */
public class Unit extends Animation {
    
    public static final int MAX_HEIGHT_TO_GO = 8;
                // разница высот которые могут преодолевать юниты

    public final static int defaultColor = 0xFFFFFF;
    public final static int greenColor = 0xFF00;
    public final static int orangeColor = 0xFFA500;
    public final static int invisColor = 0x99000000;
    public final static Image silinceIm = Image.loadImage("gui/actionpanel_i184.png");
    public final static int maxSpeed = 512;
    public final static int minSpeed = 100;

    private final static String infoStr[] = new String[6];

    public static final int MAX_ATTACK_UNITS = 16;
    public static Unit aims[] = new Unit[ MAX_ATTACK_UNITS ];
    public static int damage, add_speed, addPerSpeed;
    public static float addPerMPReg, addPerHPReg;

    //public Rectangle rect;
    public int dh, ID;

    public final static int TRUE_STRIKE = 1;
    public final static int IS_SILENCE = 2;
    public final static int CANT_AUTO_ATTACK = 4;
    public final static int DELETE_WD = 8;
    public final static int MINI_MAP_VIS = 0x10;
        // физическая, магическая и полная неуязвимость
    public final static int PYS_IMMORTAL    = 0x20;
    public final static int MAG_IMMORTAL    = 0x40;
    public final static int IS_IMMORTAL     = 0x80;
    public final static int DRAW_UNIT_INFO  = 0x100;
    public final static int CANT_ROTATE     = 0x200;
    public final static int CANT_MOVE       = 0x400;
    public final static int NOT_DRAW_HP     = 0x800;
    public final static int NOT_SELECTABLE  = 0x1000;
    public final static int HAVENT_SHADOW   = 0x2000;
    public final static int IS_BULDING      = 0x4000;
    public final static int CANT_USE_SPELLS = 0x8000;
    public final static int CANT_USE_ITEM   = 0x10000;
    public final static int CANT_DROP_ITEM  = 0x20000;
    public final static int CANT_TAKE_ITEM  = 0x40000;
    public final static int IS_ALIVE        = 0x80000;
    public final static int IS_SELECT       = 0x100000;
    public final static int BREAK_SPELL     = 0x200000;

    public final static int AUTO_ATTACK_ALOW = PYS_IMMORTAL | CANT_AUTO_ATTACK;

    public int status;

    public float attackHP; // кол-во хп (в процентах), для атаки дружественными войсками

    public int rVis; // радиус видимости в квадрате
    public int color; // цвет фильтра юнита
    public boolean canAutoAttack;

    public ULevelCreep uLvl;
    public UnitData uData;
    public int timeDie; // время после которого "труп" исчезает
    public int yPos, hpY; // hp - высота, на которой рисуется HUD юнита
    public PlayerGroup owners;
    public int isVisible, visGRP, frendGRP, inInvis, canControl, canBeTaken;
    public Unit killer;

    // Зарезервированные руппы юнитов
    public Unit VISnext; // динамическая группа,
    // которая содержит видимых юнитов
    // (содержит только перемещающихся юнитов)

        // Стек действий
    public Action actions, lastAct;

        // Данные скилов
    public Spell attribSpell;
    public Spell spells[];
    public Spell lastSpell;
    public int startS, numS;
        // Стек аур
    public Spell aurs;
        // Стек эффектов действующих на юнита
    public Effect effects;//, lastEffect;

        // Артефакты юнита
    public Unit[] items;

        // Номер лавки поблизости которой находится данный юнит
    public int shopID;

        // Если юнит является артифактом, то должен иметь это поле не null
    public Item item;

        // Предидущий елемент в очереди
    public StackObj pred;

    public Unit() {}

    public void setOwners(PlayerGroup pg) {
        //owners.setPlayer(p);
        owners = pg;
        canControl = owners.playerBit;
        visGRP = frendGRP = 0;
        Player p = pg.getPlayers();
        while (p!=null) {
            visGRP |= p.VisiblePlayers.playerBit;
            frendGRP |= p.FrendPlayer.playerBit;
            p = p.next;
        }
    }

    public int getUnitHeight(){
        return (int) (uData.unit_height * getZoom());
    }

    public int getUnitXD(){
        return px;
    }
    
    public int getUnitYD(){
        return py - yPos;
    }
    
    public void draw(Graphics g, boolean sel) {
        int xt = px;
        int yt = py;
        int r = this.r;
            
        if ((status & IS_ALIVE)!=0) {
            if ((status & HAVENT_SHADOW)==0) {
                // Тень
                //g.setColor(0x80000000);
                //g.fillOval(xt - r, yt - (r >> 1), r + r, r);
                Animation a = Game.map.getAnimation(uData.shadow_anim, r);
                if (a!=null) a.play(g, xt, yt);
            }

            if ((status & NOT_SELECTABLE)==0)
            if ((status & IS_SELECT)!=0 || sel) {
                Animation c = Game.map.getAnimation(uData.circle_anim, r);
                if (c!=null) {
                    if (!sel) {
                        if (owners.youInGroup()) {
                            g.setAlpha(defaultColor);
                        } else {
                            g.setAlpha(orangeColor);
                        }
                    } else {
                        g.setAlpha(greenColor);
                    }
                    //g.setStroke(2);
                    //g.drawOval(xt - r - 4, yt - (r >> 1) - 3, r + r + 8, r + 6);
                    c.play(g, xt, yt);
                }
            }
            yt -= yPos;
            g.setAlpha(color);
            play(g, xt, yt);
            g.setAlphaNormal();

            yt -= getUnitHeight();
            hpY = yt;
        } else {
            if (timeDie < 0) play(g, xt, yt);
        }
    }
    
    public void drawHP(Graphics g, boolean info) {
        int xt = px;
        int r = this.r;
        boolean s = (status & NOT_DRAW_HP)==0;
        if (s) {
            hpY -= 8;
            boolean isFrend = Game.yourPlayer.FrendPlayer.groupInGroup(owners);
            if (uLvl instanceof ULevelHero) {
                hpY -= infoWin.drawHeroHP(g, xt, hpY, r + r, this, isFrend);
            } else {
                hpY -= infoWin.drawCreepHP(g, xt, hpY, r + r, this, isFrend);
            }
        }
        if (info && (status & DRAW_UNIT_INFO)!=0) {
            int n = 0;
            infoStr[n++] = uData.name1;
            if (owners.name!=null) infoStr[n++] = owners.name;
            if (uLvl instanceof ULevelHero) infoStr[n++] = "Level "+uLvl.level;
            infoWin.drawWindow(g, xt, hpY - 8, infoStr, n);
        }
        if (s) {
            if ((status & IS_SILENCE)!=0) {
                hpY -= 36;
                g.draw(silinceIm, xt - 18, hpY, 36, 36);
            }
        }
    }

    public void select() {
        if ((status & IS_SELECT)==0) {
            status |= IS_SELECT;
            Animation a = Game.map.getAnimation(uData.circle_anim);
            if (a!=null) a.restart();
        }
    }

    public boolean contains(int x, int y) {
        int r = (int) (this.r * k) + 2;
        int h = (int) (uData.unit_height * k);
        int x1 = px - r;
        int y1 = py - z - h - yPos;
        int x2 = px + r;
        int y2 = y1 + h;
        if (x < x1 || y < y1) {
            return false;
        }
        return x2 > x && y2 > y;
    }

    public boolean isContains(int x, int y) {
        return (status & (IS_ALIVE | NOT_SELECTABLE))==IS_ALIVE && contains(x, y);
    }

    public void unitDieFrom(Unit uKill) {
        clearColl();
        //Game.map.steps[0].setDecoration( px>>GameMap.dxs, py>>GameMap.dxs, 0x70 );
        if (uKill!=null) killer = uKill;
        else uKill = killer;
        reset();
        setAnim(uData.die_anim);
        isCycle = false;
        Action.poolActions(this);
        inInvis = 0;
        //isVisible = -1;
        status &= ~IS_ALIVE;
        uLvl.hp = 0;
        if (items!=null)
        for (int i = items.length-1; i>=0; i--) {
            if (items[i]!=null) {
                Item it = items[i].item;
                if (it==null) {
                    randomDropItem(dropItem(i));
                } else {
                    if ((it.attrib & ITEM_DROP_WHEN_DIE)!=0)
                        randomDropItem(dropItem(i));
                }
            }
        }
        int giveLvl = uLvl.uData.give_start_skill + uLvl.uData.give_per_lvl_skill * uLvl.level;
        /*if (uKill != null) {
            if (canAutoAttack(uKill)) {
                if (uKill!=this && uKill.isAlive) {
                    uKill.uLvl.incSkill(giveLvl);
                    giveLvl >>= 1;
                }
            } else {
                giveLvl >>= 2;
            }
        }*/
        if (giveLvl!=0 && !uKill.owners.groupInGroup(frendGRP)) {
            int numU = 0;
            Unit u = (Unit) Game.allUnits.first;
            while (u != null) {
                if (u.uLvl instanceof ULevelHero && (u.status & IS_ALIVE)!=0) {
                    if (getDist(u) < uLvl.uData.radius_skill_give && !owners.groupInGroup(u.frendGRP)) {
                        numU ++;
                    }
                }
                u = (Unit) u.next;
            }
            if (numU!=0) {
                giveLvl /= numU;
                /*if ((giveLvl % numU)!=0) {
                    giveLvl++;
                }*/
                u = (Unit) Game.allUnits.first;
                while (u!=null) {
                    if (u.uLvl instanceof ULevelHero && (u.status & IS_ALIVE)!=0) {
                        if (getDist(u) < uLvl.uData.radius_skill_give && !owners.groupInGroup(u.frendGRP)) {
                            u.uLvl.incSkill(giveLvl);
                        }
                    }
                    u = (Unit) u.next;
                }
                /*do {
                    sg.unit.uLvl.incSkill(giveLvl);
                    sg = sg.next;
                } while (sg != null);*/
            }
        }
        Game.trigger.unitDie(this, killer);
    }

    public void dealsClearDamage(int dmg, Unit attacker) {
        if ((status & IS_IMMORTAL)==0 && (status & IS_ALIVE)!=0 && dmg>0) { // чистый дамаг блокируется полной неуязвистью
            int modif = 0x80000000;
            damage = dmg;
            int hp = uLvl.getCurHP();
            if (dmg>hp) damage = hp;
            else damage = dmg;
            Effect ef = effects;
            while (ef!=null) {
                if (ef.level>0 && (ef.modif & modif)==0)
                    modif |= ef.dealsClearDamage(this);
                ef = ef.next;
            }
            if (damage>=hp) hp = 0; else hp -= damage;
            uLvl.setHP(hp);
            if (hp==0) {
                unitDieFrom(attacker);
            }
        }
    }

    public void dealsMagicDamage(int dmg, Unit attacker){
        if ((status & MAG_IMMORTAL)==0) {
            dmg = uLvl.dealsMagicDamage(dmg);
            dealsClearDamage(dmg, attacker);
        }
    }

    public void dealsPhythDamage(int dmg, Unit attacker){
        if ((status & PYS_IMMORTAL)==0) {
            dmg = uLvl.dealsPhythDamage(dmg);
            dealsClearDamage(dmg, attacker);
        }
    }

    public boolean unitAct() {
        if ((status & IS_ALIVE)!=0) {
            if (actions!=null) {
                Action.acts(this);
            } else {
                standAnim();
                incAnm();
            }
            if ((status & CANT_ROTATE)==0) setAngle((int) (angle * MathUtils.radiansToDegrees));
            //getRect();
        } else {
            if (timeDie < 0) {
                timeDie += Game.tFrame;
                incAnm();
            } else if ((status & DELETE_WD)!=0) {
                relese();
                return false;
            }
        }
        return true;
    }

    public void removeFromMap() {
        //System.out.println("Remove from map ["+ID+"]: " + uData.name1+" | "+(owners!=null ? owners.name : "no name"));
        if (ID>=0) {
            if (Game.unitSelectNow==this) Game.unitSelectNow = null;
            if ((status & IS_ALIVE)!=0) clearColl();
            if (next!=null) ((Unit) next).pred = pred;
            allUnits.remove(pred, this);
            Game.AllUnitsID[ID] = null;
            Game.numUnit --;
            pred = null;
            ID = -1;
        }
    }

    @Override
    public void relese() {
        if (ID>=0) {
            // удаление юнита из стека видимых юнитов
            Unit v = Game.visUnits;
            Unit p = null;
            while (v!=null) {
                if (v==this) {
                    if (p==null) Game.visUnits = VISnext;
                    else p.VISnext = VISnext;
                    break;
                }
                p = v;
                v = v.VISnext;
            }
            VISnext = null;
            removeFromMap();
        }
        killer = null;
        owners = null;
        if (items!=null) {
            for (Unit i : items)
                if (i!=null) i.relese();
            items = null;
        }
        //u.spells = null;
        Attack.poolsModificators(effects);
        effects = null;//lastEffect = null;
        if (attribSpell!=null) {
            attribSpell.relese();
            attribSpell = null;
        }
        if (spells!=null) {
            for (Spell s : spells)
                if (s!=null) s.relese();
            spells = null;
        }
        Game.poolUnit.add(this);
        Spell.clearAurs(this);
        if (item!=null) {
            item.relese();
            item = null;
        }
        if (uLvl!=null) {
            if (uData.isHero) Game.poolHero.add(uLvl);
            else Game.poolCreep.add(uLvl);
            uLvl = null;
        }
    }

    public void setMoveAnim() {
        // установка анимации на передвижение
        //int speed = getSpeed();
        if( speed<=0 ) setAnim(uData.move_anim); else
        setAnim(uData.move_anim, 300f / speed );//- getSpeed());
        isCycle = true;
    }

    public void teleport( int x, int y ) {
        Unit u = (Unit) Game.allUnits.first;
        while (u != null) {
            Attack a = u.uLvl.getCurAttack();
            if (a!=null && a.aim == this) a.aim = null;
            u = (Unit) u.next;
        }
        specEffect se = Game.postEffects.getEffects();
        while( se!=null ){
            if (se instanceof Bullet && ((Bullet) se).uMove == this) {
                ((Bullet) se).uMove = null;
            }
            se = (specEffect) se.next;
        }
        setPos(x, y);
    }

    public void standAnim() {
        // анимация - стоять
        setAnim(uData.stand_anim);
        isCycle = true;
    }

    public void attackAims() {
        Attack a = uLvl.getCurAttack();
        if (a!=null) {
            Unit p = a.aim;
            //setDirFromVec( p.px-px, p.py-py );
            a.dealsDamageToAims(this, p);
            /*int i = a.numAttack - 1;
            if (i>0) {
                aims[0] = p;
                int j=1;
                Unit u = Game.allUnits;
                while( u!= null ){
                    
                    if( u!=aim && canAutoAttack(u) ) {
                        if (u.isAlive && owners.groupInGroup(u.isVisible) && getDist(u) < a.radius) {
                            Damage dmg = (Damage) Effect.addEffect(null, u, next);
                            dmg.effects = dmg.next = null;
                            dmg.dmg = mDmg;
                            a.aim = u;
                            if ((a = a.next) == null) {
                                break;
                            }
                        }
                    }
                    u = u.ALLnext;
                }
                for (; i>0; i--) {
                    
                }
            }*/
        }
    }

    public Player getOwnerPlayer() {
        Player p = null;
        int o = owners.playerBit;
        int i = 0;
        while (o!=0) {
            if ((o & 1)!=0) {
                if ((o & 0xFFFFFFFE)!=0) break;
                p = ClientServer.players[i];
            }
            i ++;
            o >>= 1;
        }
        return p;
    }

    public boolean canFrendAttack(){
        return getHPper()<=attackHP;
    }

    public boolean canAutoAttack(Unit aim) {
        if ((aim.status & AUTO_ATTACK_ALOW)!=0) return false;

        /*for (int i = Server.ALL_PLAYER - 1; i >= 0; i--) {
            PlayerGroup fg = Game.players[i].FrendPlayer;
            if (fg.groupInGroup(owners)) {
                if (fg.groupInGroup(aim.owners)) {
                    return false;
                }
            }
        }*/
        return !owners.groupInGroup(aim.frendGRP);
    }

    public void setMovePos(int x, int y, boolean add) {
        if ((status & IS_ALIVE)!=0) {
            uLvl.clearAttacks();
            moveToPos a = Action.setAction(this, moveToPos.class, add);
            if (a!=null) {
                a.setAimXY(this, x, y);
            }
        }
    }

    public void setMoveToUnit(Unit u, boolean add) {
        if ((status & IS_ALIVE)!=0 && u!=this) {
            uLvl.clearAttacks();
            moveToUnit a = Action.setAction(this, moveToUnit.class, add);
            if (a!=null) {
                a.setAimUnit(this, u);
            }
        }
    }

    public void setAttackPos(int x, int y, boolean add) {
        if ((status & IS_ALIVE)!=0) {
            moveToPos a = Action.setAction(this, moveToPos.class, add);
            if (a!=null) {
                a.setAutoAttack();
                a.setAimXY(this, x, y);
            }
        }
    }

    public void setAttackUnit(Unit u, boolean auto, boolean add) {
        if ((status & IS_ALIVE)!=0 && u!=null && (u.status & IS_ALIVE)!=0) {
            if (u.owners.groupInGroup(frendGRP)) {
                if (!u.canFrendAttack()) {
                    setMoveToUnit(u, add);
                    return;
                }
            }
            Attack a = uLvl.incAttack();
            if (spells!=null)
            for (Spell s : spells) {
                if (s!=null && a==null) a = s.getCastAttack();
            }
            //uLvl.nextAttack = a;
            //a = uLvl.incAttack();
            if (a!=null) {
                if (u!=this && u!=null) {
                    uLvl.setAim(u);
                    if (auto) {
                        AttackAct act = Action.setAction(this, AttackAct.class, add);
                        if (act!=null) act.setAttack(this, a);
                    } else {
                        if (actions==null || (actions.flags & Action.alreadyAttack)==0) {
                            AttackAct act = Action.addAutoAttack(this, AttackAct.class);
                            if (act!=null) {
                                act.setAttack(this, a);
                                act.setAutoAttack();
                            }
                        }
                    }
                }
            } else
                setMoveToUnit(u, add);
        }
    }

    public void castSpellToUnit(Unit u, Spell s, boolean add) {
        if (u!=null && (status & CANT_USE_SPELLS)==0) {
            //boolean frend = owners.groupInGroup(u.frendGRP);
            //boolean isHero = u.uLvl instanceof ULevelHero;
            int i = s.influents;
            boolean yourUnit = (this==u);
            if ((i & Spell.YOUR_AURA)==0) yourUnit ^= true;
            if (yourUnit && (status & IS_ALIVE)!=0 && (u.status & IS_ALIVE)!=0
                    && isInfluents(u, i)) {
                s.isCast = true;
                Attack a = s.getCastAttack();
                if (a==null) a = uLvl.incAttack();
                if (a!=null) {
                    a.aim = u;
                    AttackAct act = Action.setAction(this, AttackAct.class, add);
                    if (act!=null) {
                        act.setAttack(this, a);
                        act.stopAfterFirst();
                    }
                } else
                    setMoveToUnit(u, add);
            }
        }
    }

    public void castSpellToPos(int x, int y, Spell s, boolean add) {
        if ((status & CANT_USE_SPELLS)==0) {
            s.isCast = true;
            CastSpellToPos act = Action.setAction(this, CastSpellToPos.class, add);
            if (act!=null) act.setSpell(s, x, y);
        }
    }

    public float getHPper(){
        return (float) uLvl.getCurHP() / uLvl.max_hp;
    }
    
    public float getMPper(){
        return (float) uLvl.getCurMana() / uLvl.max_mp;
    }

    public void setHPper(float percent){
        uLvl.hp = uLvl.max_hp * percent;
    }
    
    public void setMPper(float percent){
        uLvl.mp = uLvl.max_mp * percent;
    }

    public void refreshEffets() {
        // Сброс автоатаки
        Attack a = uLvl.getCurAttack();
        if (a!=null ) {//&& a.aim==null) {
            a.autoDist = Integer.MAX_VALUE;
            canAutoAttack = Action.canAutoAttack(this);
        } else canAutoAttack = false;
        // Сброс атрибутов
        uLvl.resetAttrib();
        status &= ~(IS_SILENCE | TRUE_STRIKE);
        inInvis = 0;
        color = defaultColor;
        //status |= MINI_MAP_VIS;
        shopID = -1;
        speed = uData.start_speed;
        addPerMPReg = addPerHPReg = add_speed = addPerSpeed = 0;
        // Обновление скиллов
        if (spells!=null)
        for (Spell s : spells) s.refreshSpell();
        // Обновление эффектов изменяющих статы
        Effect.refreshStats(this);
        // Установка скорсти
        //speed += add_speed;
        //if (addPerSpeed!=0) speed += (speed * addPerSpeed) / 100;
        // Установка атрибутов
        uLvl.refreshMainAttrib();
        if (addPerMPReg!=0) uLvl.mpReg += uLvl.mpReg * addPerMPReg;
        if (addPerHPReg!=0) uLvl.hpReg += uLvl.max_hp * addPerHPReg;
        uLvl.refreshAttrib(this);
        // Обновление эффектов наносящих урон и т. д.
        //add_speed = addPerSpeed = 0;
        Effect.refreshDamage(this);
        // Установка скорсти
        speed += add_speed;
        if (addPerSpeed!=0) speed += (speed * addPerSpeed) / 100;
        if (speed>maxSpeed) speed = maxSpeed;
        if (speed<minSpeed) speed = minSpeed;
        setSpeed(speed);
        // Сброс собственных аур
        resetAurs();
        status &= ~ BREAK_SPELL;
    }

    public void setLastSpell(Spell last) {
        lastSpell = last;
        last.attrib |= Spell.SPELL_CAST_NOW;
    }

    public void postClear() {
        if (lastSpell!=null) {
            lastSpell.attrib &= ~Spell.SPELL_CAST_NOW;
        }
    }

    public void resetAurs() {
        Spell aura = aurs;
        while (aura!=null) {
            Effect ae = aura.auraEffect;
            if ((aura.attrib & Spell.AURA_DISABLED)==0)
            if (ae!=null && ((aura.attrib & Spell.UNIT_AURA_OFF)!=0
                    || (aura.influents & YOUR_AURA)!=0)) {
                ae.addEffect(this);
            }
            aura = (Spell) aura.next;
        }
    }

    public boolean isInfluents(Unit u, int i) {
        if (u==this && (i & YOUR_AURA)!=0) return true;
        boolean frend = owners.groupInGroup(u.frendGRP);
        boolean isHero = u.uLvl instanceof ULevelHero;
        boolean isTower = (u.status & IS_BULDING)!=0;
        boolean isItem = u.item != null;
        int at = u.uLvl.getAttackType();
        if (at==0) at = 1;
        return ((frend && (i & Spell.ALIES_AURA)!=0) || ((!frend && (i & Spell.ENEMY_AURA)!=0))) &&
                ((at==1 && (i & Spell.MEELE_UNITS_AURA)!=0) || (at==2 && (i & Spell.RANGE_UNITS_AURA)!=0)) &&
                ((isHero && (i & Spell.HERO_AURA)!=0) || (!isHero && (i & Spell.CREEPS_AURA)!=0) ||
                (isTower && (i & Spell.TOWERS_AURA)!=0)) && !(isItem ^ (i & Spell.ITEMS_UNITS_AURA)!=0);
    }

    public void refreshAurs( Unit u, int dist ){
        Spell aura = aurs;
        while (aura!=null) {
            Effect ae = aura.auraEffect;
            if ((aura.attrib & Spell.AURA_DISABLED)==0) {
                if (ae!=null) {
                    if (ae.level>0 && (aura.radiusS<0 || dist<=aura.radiusS)) {
                        if (isInfluents(u, aura.influents)) {
                            if ((aura.attrib & Spell.UNIT_AURA_ON)!=0) {
                                ae.addEffect(this);
                            } else if ((aura.attrib & Spell.UNIT_AURA_OFF)!=0) {
                                ae.isDelete = true;
                            } else {
                                ae.addUnicUnitEffect(u);
                            }
                        }
                    }
                } else {
                    // значит это лавка
                    if ((u.status & CANT_TAKE_ITEM)==0 && (aura.radiusS<0 || dist<=aura.radiusS)) {
                        u.shopID = aura.number;
                        int p = u.uData.shopPrior;
                        if ((u.status & IS_SELECT)!=0) p += Shop.ADD_SELECT_UNIT_PRIORETET;
                        u.owners.setShop(u.shopID, p);
                    }
                }
            }
            aura = (Spell) aura.next;
        }
    }

    public int getShopPrior() {
        return (status & IS_SELECT)!=0 ? uData.shopPrior + Shop.ADD_SELECT_UNIT_PRIORETET : uData.shopPrior;
    }

    public Unit dropItem(int i) {
        if ((status & CANT_DROP_ITEM)==0)
        if (items!=null) {
            Unit it = items[i];
            if (it!=null) {
                Item item = it.item;
                if (item!=null) {
                    if (item.spell!=null) {
                        Effect e;
                        if ((e = item.spell.getEffect())!=null) e.dropItem(this, it, i);
                    }
                    if ((item.attrib & ITEM_CANT_BE_DROPED)!=0) return null;
                }
                removeItem(i);
                return it;
            }
        }
        return null;
    }

    public void randomDropItem(Unit it) {
        if (it!=null) {
            int x = px + (ClientServer.random.nextInt(r) << 2) - r - r;
            int y = py + (ClientServer.random.nextInt(r) << 2) - r - r;
            it.setPos(x, y);
            Game.addUnit(it);
        }
    }

    public boolean giveItem(Unit item) {
        if ((status & CANT_TAKE_ITEM)==0)
        if (item!=null && items!=null) {
            Item it = item.item;
            if (it!=null && (it.attrib & ITEM_IN_ONE_SLOT)!=0) {
                for (int j=0; j<items.length; j++) {
                    Unit i = items[j];
                    if (i!=null && i!=item && i.uData==item.uData) {
                        Spell spell = i.item.spell;
                        if (spell!=null) {
                            int max = it.iData.maxItemsInSlot;
                            int last = it.spell.number;
                            int num = spell.number + last;
                            if (max<0 || num<=max) it.spell.number = 0;
                            else {
                                it.spell.number = num - max;
                                num = max;
                            }
                            if (last!=it.spell.number) {
                                Effect e = spell.getEffect();
                                spell.number = num;
                                if (e!=null) e.giveItem(this, item, j);
                                setCommonCooldown(it);
                            }
                            if (it.spell.number==0) {
                                item.relese();
                                return true;
                            } else
                                return false;
                        }
                    }
                }
            }
            for (int i=0; i<items.length; i++) {
                if (items[i]==item) return false;
                if (items[i]==null) return addItem(item, i);
            }
        }
        return addItem(item, -1);
    }

    public boolean addItem(Unit Item, int i) {
        Item it;
        if ((it = Item.item)!=null && Item.owners.groupInGroup(owners)) {
            //if (it.number==0) return false;
            boolean t;
            boolean c;
            if (it.iData.nextItems!=null)
            for (UnitData id : it.iData.nextItems) {
                t = true;
                int min = items.length - 1;
                for (Unit itm : items) {
                    if (itm!=null && itm.item!=null)
                        itm.item.attrib &= ~REMOVE_ITEM;
                }
                if (id.itemData.receptItems!=null) {
                    UnitData ud = Item.uData;
                    for (UnitData ir : id.itemData.receptItems) {
                        if (ir!=ud) {
                            c = false;
                            for (int j=0; j<items.length; j++) {
                                Unit itemt = items[j];
                                if (itemt!=null && itemt.owners.groupInGroup(owners) && ir==itemt.uData) {
                                    Item itm = itemt.item;
                                    if (itm!=null && (itm.attrib & REMOVE_ITEM)==0) {
                                        itm.attrib |= REMOVE_ITEM;
                                        if (j<min) min = j;
                                        c = true; break;
                                    }
                                }
                            }
                            if (!c) { t = false; break; }
                        } else ud = null;
                    }
                }
                if (t) {
                    deleteItems();
                    Item.relese();
                    return addItem(id.createUnit(owners), min);
                }
            }
            if (i>=0 && it.spell!=null) {
                it.spell.setSpell(this);
                Effect e = it.spell.getEffect();
                if (e!=null) e.giveItem(this, Item, i);
                setCommonCooldown(it);
            }
        }
        if (i>=0) {
            items[i] = Item;
            return true;
        } else {
            return false;
        }
    }

    public Unit getItem(int slot) {
        if (items!=null && slot<items.length) return items[slot];
        return null;
    }

    public void clearItemRemoveData() {
        if (items!=null)
        for (Unit itm : items) {
            if (itm!=null && itm.item!=null)
                itm.item.attrib &= ~REMOVE_ITEM;
        }
    }

    public boolean haveItem(UnitData it) {
        if (items!=null)
        for (Unit itemt : items) {
            if (itemt!=null && itemt.owners.groupInGroup(owners) && it==itemt.uData) {
                Item itm = itemt.item;
                if (itm!=null && (itm.attrib & REMOVE_ITEM)==0) {
                    itm.attrib |= REMOVE_ITEM;
                    return true;
                }
            }
        }
        return false;
    }

    public void setCommonCooldown(Item it) {
        if (it!=null && it.iData.commonCooldown!=null)
        for (Unit item : items) {
            if (item!=null && item.item!=null && item.item!=it && item.item.spell!=null
                    && item.item.isEqualsItems(it.iData.commonCooldown)) {
                it.spell.setCooldouwnTimeEnd(item.item.spell);
            }
        }
    }

    public void deleteItems() {
        for (int i=0; i<items.length; i++) {
            Unit itm = items[i];
            if (itm!=null && itm.item!=null && (itm.item.attrib & REMOVE_ITEM)!=0) {
                removeItem(i);
                itm.relese();
            }
        }
    }

    public void deleteItem(int i) {
        Unit item = items[i];
        removeItem(i);
        if (item!=null) item.relese();
    }

    public void deleteItem(Unit item) {
        for (int i=items.length-1; i>=0; i ++) {
            if (items[i]==item) removeItem(i);
        }
        item.relese();
    }

    public void removeItem(int i) {
        Unit item = items[i];
        if (item!=null) {
            if (item.owners.groupInGroup(owners)) {
                Item it = item.item;
                if (it!=null) {
                    Spell s = it.spell;
                    if (s!=null) s.deleteSpell(this);
                } 
            }
            items[i] = null;
        }
    }

    public void changeItemSlot(int item1, int item2) {
        if (items!=null && (status & CANT_USE_ITEM)==0) {
            Unit it = items[item1];
            items[item1] = items[item2];
            items[item2] = it;
        }
    }

    public void useItemOnUnit(int slot, Unit aim, boolean add) {
        useItemOnUnit act = Action.setAction(this, useItemOnUnit.class, add);
        if (act!=null) act.setAimUnit(this, aim, slot);
    }

    public void useItemToPos(int slot, int x, int y, boolean add) {
        useItemToPos act = Action.setAction(this, useItemToPos.class, add);
        if (act!=null) act.setAimUnit(this, slot, x, y);
    }

    public void useItemAlready(int slot, Player owner, boolean add) {
        useItemAlready act = Action.setAction(this, useItemAlready.class, add);
        if (act!=null) act.setItem(slot);
    }

    public void dropItemToUnit(int item, Unit aim, boolean add) {
        DropItemToUnit act = Action.setAction(this, DropItemToUnit.class, add);
        if (act!=null) act.setAimUnit(this, item, aim);
    }

    public void dropItemToPos(int item, int x, int y, boolean add) {
        DropItemToPos act = Action.setAction(this, DropItemToPos.class, add);
        if (act!=null) act.setAimXY(this, item, x, y);
    }

    public float x, y;
    public int r; // радиус юнита
    public int px, py, z; // координаты x, y (настоящие)
    public int cType; // тип коллизии
    //public int angle; // угол направление юнита
    public float angle;
    public int cMask; // маска коллизии
    private int speed;
    //public Unit uMove; // юнит за которым данный юнит будет следовать

    public void setData(UnitData uData, int ct, int cm) {
        setData(uData.aData);
        setZoom(uData.zoom);
        ID = -1;
        this.uData = uData;
        isVisible = visGRP;
        status = uData.unitStatus;
        attackHP = uData.attackHP;
        isVisible = visGRP = frendGRP = inInvis = canControl = canBeTaken = 0;
        //Stand = new Image[maxDir][];
        //Walk = new Image[maxDir][];
        //imTemp = im;
        //pos = new UnitPosDynamic( 200, 200, 14, GameMap.COLLISION_UNIT, 7, 300 );
        //rect = new Rectangle();
        //name1 = "Mortra";
        //owners = new PlayerGroup(0, null);
        setAnim( 0 );
        //isVisible = new PlayerGroup(0, null);
        //rVis = 600 * 600;
        //getRect();
        int n = uData.numItems;
        if (n>0) items = new Unit[n];
        r = uData.radius;
        cType = ct;
        setSpeed(uData.start_speed);
        cMask = cm;
    }

    public Unit(UnitData uData, int ct, int cm) {
        setData(uData, ct, cm);
    }

    public void setDirToPos( int x, int y ){
        angle = (float) MathUtils.atan2( y - py, x - px );
        //angle = (int) (MathUtils.radiansToDegrees * angle);
    }

    public void setDirToUnit( Unit u ){
        angle = (float) MathUtils.atan2( u.py - py, u.px - px );
        //angle = (int) (MathUtils.radiansToDegrees * angle);
    }

    public void setDirFromVec( int vx, int vy ){
        angle = (float) MathUtils.atan2( vy, vx );
        //angle = (int) (MathUtils.radiansToDegrees * angle);
    }
 
    public void setDirection(int dir) {
        //angle = dir;
        angle = dir * MathUtils.degreesToRadians;
    }

    public void setSpeed(int sp) {
        if (cur_anim == uData.move_anim) {
            if( sp<=0 ) setTime( 1 ); else
            setTime(300f / sp);//- getSpeed());
        }
        speed = sp;
        //if( num!=0 ) setMovePos( x, y, false );
    }

    public int getSpeed(){ return speed; }

    public boolean setNewPos(int xp, int yp){
        if ((status & CANT_MOVE)==0) {
            int ssz = GameMap.dxs;
            GameMap m = Game.map;
            int dx = px>>ssz; int dy = py>>ssz;
            float s = speed * Game.deltaT;
            setDirToPos( xp, yp );
            float vx = s * MathUtils.cos(angle);
            float vy = s * MathUtils.sin(angle);
            float xt = x + vx; float yt = y + vy;
            int a = (int) xt; int b = (int) yt;
            int tx = a>>ssz;
            int ty = b>>ssz;
            if (dx!=tx || dy!=ty) {
                int h = m.canMove(tx, ty, cMask, z);
                if (h>=0) {
                    if (cType!=0) {
                        m.clearMapMask( dx, dy, cType );
                        m.setMapMask( tx, ty, cType );
                    }
                    this.z = h;
                    x = xt; y = yt;
                    px = a; py = b;
                    return isEndMove(xp, yp, s);
                }
            } else {
                x = xt; y = yt;
                px = a; py = b;
                return isEndMove(xp, yp, s);
            }
        }
        return true;
    }

    public boolean isEndMove(int xp, int yp, float s){
        float dx = xp - x;
        float dy = yp - y;
        if (dx<0) dx = - dx;
        if (dy<0) dy = - dy;
        if (dx<=s && dy<=s) {
            setPos(xp, yp);
            return true;
        }
        return false;
    }

    public void setPos( int xt, int yt ){
        if( cType!=0 ){
            int ssz = GameMap.dxs;
            GameMap m = Game.map;
            if( x!=xt || y!=yt ){
                int dx = px>>ssz;
                int dy = py>>ssz;
                m.clearMapMask( dx, dy, cType );
            }
            int dx = xt>>ssz;
            int dy = yt>>ssz;
            this.z = (m.getMapMask( dx, dy ) >> 8) & 255;
            m.setMapMask( dx, dy, cType );
        }
        x = xt; y = yt;
        px = xt;
        py = yt;
    }

    public void clearColl(){
        int ssz = GameMap.dxs;
        Game.map.clearMapMask( px>>ssz, py>>ssz, cType );
    }

    public void setXYReal(float x, float y){
        setPos((int) x, (int) y);
        this.x = x;
        this.y = y;
    }
    
    public int getDist( Unit u ){
        int x = px - u.px;
        int y = py - u.py;
        return x * x + y * y;
    }

    public int getDist(int x, int y){
        x -= px; y -= py;
        return x * x + y * y;
    }

    public int getZ(){
        return z + yPos;
    }
}
