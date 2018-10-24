package art.soft.gui.buttons.inGame;

import art.soft.Game;
import static art.soft.Game.CShost;
import static art.soft.Game.CURSOR_ATTACK;
import static art.soft.Game.CURSOR_ATTACK_ILLEGAL;
import static art.soft.Game.CURSOR_ATTACK_TEAM;
import static art.soft.Game.CURSOR_DEFAULT_ENEMY;
import static art.soft.Game.CURSOR_DEFAULT_TEAM;
import static art.soft.Game.CURSOR_LEARN_ABILITY;
import static art.soft.Game.CURSOR_SPELL_DEFAULT;
import static art.soft.Game.CURSOR_SPELL_ILLEGAL;
import static art.soft.Game.CURSOR_SPELL_WALK_TO;
import static art.soft.Game.DO_ATTACK;
import static art.soft.Game.DO_LEVEL_UP;
import static art.soft.Game.DO_NOTHING;
import static art.soft.Game.DO_SELECT_ITEM;
import static art.soft.Game.DO_SELECT_SPELL;
import static art.soft.Game.DO_SELECT_UNITS;
import static art.soft.Game.castSpell;
import static art.soft.Game.circleR;
import static art.soft.Game.circleX;
import static art.soft.Game.circleY;
import static art.soft.Game.curCam;
import static art.soft.Game.curCur;
import static art.soft.Game.getInfo;
import static art.soft.Game.mRect;
import static art.soft.Game.mbut1;
import static art.soft.Game.mbut2;
import static art.soft.Game.onGui;
import static art.soft.Game.ringAOE;
import static art.soft.Game.spellNum;
import static art.soft.Game.statusKey;
import static art.soft.Game.unitSelectNow;
import static art.soft.Game.yourPlayer;
import art.soft.Graphics;
import art.soft.gui.Interface;
import art.soft.gui.buttons.DragTarget;
import art.soft.gui.buttons.DraggedBut;
import art.soft.gui.buttons.IButton;
import static art.soft.map.mapEffect.EFFECT_ATTACK;
import static art.soft.map.mapEffect.EFFECT_ATTACK_WAY;
import static art.soft.map.mapEffect.EFFECT_MOVE_TO;
import static art.soft.map.mapEffect.EFFECT_MOVE_WAY;
import art.soft.spells.Spell;
import art.soft.units.Unit;
import static art.soft.units.Unit.CANT_TAKE_ITEM;
import static art.soft.units.Unit.PYS_IMMORTAL;
import art.soft.utils.Rectangle;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Artem
 */
public class GameControl extends IButton implements DragTarget {

    public static GameControl render;
    public int win[];

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        render = this;
    }

    public void preRender(Graphics g) {
        if (Game.HUD==Game.inGame) {
            g.setProjectionMatrix(Game.hudCam.combined);
            g.setStroke(1);
            if(statusKey==DO_SELECT_UNITS) {
                Rectangle r = curCam.scrRect;
                g.setColor(Color.GREEN);
                g.drawRect(r.x, r.y, r.width, r.height);
                getInfo = r.width==0 && r.height==0;
            }
            if (!onGui && (statusKey==DO_SELECT_SPELL || statusKey==DO_SELECT_ITEM)) {
                int r = castSpell.radiusAOE;
                if (r>0) {
                    Rectangle c = curCam.scrRect;
                    g.draw(ringAOE, c.x-r, c.y-r, r+r, r+r);
                }
            }
            /*if (!onGui) {
                Rectangle c = curCam.scrRect;
                g.setFont(Game.getFont(18, true));
                g.drawString(""+statusKey, c.x, c.y-20);
            }*/
            g.end();
            g.begin();
            g.setProjectionMatrix(curCam.combined);
            if (circleR>0) {
                g.setColor( Color.GREEN );
                g.setStroke(1);
                g.drawOval(circleX - circleR, circleY - circleR, circleR + circleR, circleR + circleR);
                circleR = 0;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        switch (statusKey) {
        case DO_ATTACK :
            curCur = CURSOR_ATTACK;
            break;
        case DO_SELECT_ITEM :
        case DO_SELECT_SPELL :
            curCur = CURSOR_SPELL_DEFAULT;
            break;
        case DO_LEVEL_UP :
            curCur = CURSOR_LEARN_ABILITY;
            break;
        }
        onGui = true;
    }

    @Override
    public void pressed(int code, int k) {
            // скрыть окно лавки
        if (mbut1) Interface.getWindow(win).hideWin();

        boolean frend = false; boolean enemy = false;
        Unit sel = yourPlayer.gUnit.unit;
        int grp = sel==null ? yourPlayer.FrendPlayer.playerBit : sel.frendGRP;
        if (unitSelectNow!=null) {
            if (unitSelectNow.owners!=null && unitSelectNow.owners.groupInGroup(grp)) {
                frend = true; curCur = CURSOR_DEFAULT_TEAM;
            } else {
                enemy = true; curCur = CURSOR_DEFAULT_ENEMY;
            }
            //isTower = unitSelectNow instanceof Unit;
        }
        switch (statusKey) {
        case DO_ATTACK :
            if (mbut1) {
                statusKey = DO_NOTHING;
                if (unitSelectNow!=null && unitSelectNow!=yourPlayer.gUnit.selUnit) CShost.setAttackUnit(unitSelectNow);
                else {
                    CShost.setAttackPos( mRect.x, mRect.y );
                    Game.map.addEffect(mRect.x, mRect.y, Game.shift ? EFFECT_ATTACK_WAY : EFFECT_ATTACK, 1f, true, -1);
                }
            }
            if (unitSelectNow!=null && (unitSelectNow.status & PYS_IMMORTAL)!=0)
                curCur = CURSOR_ATTACK_ILLEGAL;
            else {
                if (frend) curCur = unitSelectNow.canFrendAttack() ? CURSOR_ATTACK_TEAM : CURSOR_ATTACK_ILLEGAL;
                else curCur = CURSOR_ATTACK;
            }
            break;
        case DO_SELECT_ITEM :
        case DO_SELECT_SPELL :
            int atr = castSpell.attrib;
            curCur = CURSOR_SPELL_DEFAULT;
            if ((atr & Spell.CAST_ON_UNIT)!=0 && unitSelectNow!=null) {
                if (castSpell.getSpellOwner().isInfluents(unitSelectNow, castSpell.influents)) {
                    curCur = CURSOR_SPELL_WALK_TO;
                    if (mbut1) {
                        if (statusKey==DO_SELECT_ITEM)
                            CShost.useItemOnUnit(spellNum, unitSelectNow);
                        else CShost.castSpellOnUnit(spellNum, unitSelectNow);
                        statusKey = DO_NOTHING;
                        break;
                    }
                } else curCur = CURSOR_SPELL_ILLEGAL;
            }
            if (mbut1 && (atr & Spell.CAST_ON_AOE)!=0) {
                if (statusKey==DO_SELECT_ITEM)
                    CShost.useItemlToPos(spellNum, mRect.x, mRect.y);
                else CShost.castSpellToPos(spellNum, mRect.x, mRect.y);
                statusKey = DO_NOTHING;
            }
            break;
        case DO_LEVEL_UP :
            curCur = CURSOR_LEARN_ABILITY;
            if (mbut1) statusKey = DO_NOTHING;
            break;
        case DO_SELECT_UNITS :
            if (code==Interface.BUTTON_RELESSED) {
                yourPlayer.gUnit.getUnitsFromVISRect(mRect);
                statusKey = DO_NOTHING;
            }
            break;
        default:
            if (mbut1) {
                if (Game.control) {
                    CShost.playerPing(mRect.x, mRect.y, true);
                } else if (Game.alt) {
                    CShost.playerPing(mRect.x, mRect.y, false);
                } else
                    statusKey = DO_SELECT_UNITS;
            }
        }
        if (mbut2) {
            if (statusKey==DO_NOTHING) {
                if (unitSelectNow!=null && unitSelectNow!=yourPlayer.gUnit.selUnit) CShost.setMoveToUnit(unitSelectNow);
                else {
                    CShost.setMovePos(mRect.x, mRect.y);
                    Game.map.addEffect(mRect.x, mRect.y, Game.shift ? EFFECT_MOVE_WAY : EFFECT_MOVE_TO, 1f, true, -1);
                }
            }
            statusKey = DO_NOTHING;
        }
        getInfo = statusKey==DO_NOTHING;
        onGui = false;
    }

    @Override
    public void draggedOver(DraggedBut target) {
        if (target instanceof ItemBut) {
            if (unitSelectNow!=null && unitSelectNow!=yourPlayer.gUnit.selUnit
                    && (unitSelectNow.status & CANT_TAKE_ITEM)==0)
                curCur = CURSOR_SPELL_WALK_TO;
            else curCur = CURSOR_SPELL_DEFAULT;
            onGui = false;
        }
    }

    @Override
    public void DropIn(DraggedBut target) {
        if (target instanceof ItemBut) {
            if (unitSelectNow!=null && unitSelectNow!=yourPlayer.gUnit.selUnit
                    && (unitSelectNow.status & CANT_TAKE_ITEM)==0)
                CShost.dropItemToUnit(((ItemBut) target).num, unitSelectNow);
            else {
                CShost.dropItemToPos(((ItemBut) target).num, mRect.x, mRect.y);
            }
        }
    }
}
