package art.soft.units.actions;

import art.soft.units.Action;
import art.soft.units.Attack;
import art.soft.units.Unit;
import static art.soft.units.Unit.IS_ALIVE;

/**
 *
 * @author Artem
 */
public class AttackAct extends moveToAim {

    private static final int STOP_AFTER_FIRST_ATTACK = 0x10000000;
    private Attack a;
    private boolean autoAttack;
    private int attackStatus;

    @Override
    public void Init (Unit u) {
        flags = Action.canInterruptAction | Action.alreadyAttack;
        autoAttack = false;
        attackStatus = 1;
    }

    public void stopAfterFirst() {
        flags |= STOP_AFTER_FIRST_ATTACK;
    }

    @Override
    public void postInit (Unit u) {
        u.uLvl.nextAttack = a;
        u.uLvl.incAttack();
    }

    @Override
    public boolean meetWithUnit(Unit u, Unit aim) {
        return false;
    }

    public void setAttack(Unit u, Attack attack) {
        a = attack;
        postInit(u);
    }

    @Override
    public void setAutoAttack() {
        flags |= deleteThisAction;
        autoAttack = true;
    }

    public void move(Unit u) {
        if (moveUnit(u, a.aim, 0))
            attackStatus = 3;
    }

    public void setAttackStatus(Unit u) {
        if ((a.aim!=null && (a.aim.status & IS_ALIVE)!=0 && u.owners.groupInGroup(a.aim.isVisible))) {
            int x = a.aim.px - u.px;
            int y = a.aim.py - u.py;
            int dist = x * x + y * y;
            if (dist < a.radius) {
                //unitStatus = 2;
                //u.setVec(x, y);
                u.reset();
                // Начало атаки
                u.setAnim(a.attack_anim, a.attackTime);
                a.startDealsDamage(u);
                attackStatus = 2;
                u.setDirFromVec(x, y);
                u.isCycle = false;
            } else {
                if (a.aim!=null && !u.owners.groupInGroup( a.aim.isVisible )) {
                    a.aim = null;
                    attackStatus = 3;
                } else {
                    //u.setMoveAnim();
                    attackStatus = 1;
                }
            }
        } else {
            a.aim = null;
            attackStatus = 3;
        }
    }

    @Override
    public boolean act (Unit u) {
        if (a!=null) {
            if (attackStatus==0) {
                if (u.incAnm()) {
                    a = u.uLvl.incAttack();
                    attackStatus = (flags & STOP_AFTER_FIRST_ATTACK)==0 ? 1 : 3;
                }
            }
            if (attackStatus==1) {
                if (autoAttack) flags |= canAutoAttack;
                setAttackStatus(u);
                if (attackStatus==1) move(u);
                else flags &= ~canAutoAttack;
            }
            if (attackStatus==2) {
                if (u.incAnm() || u.getProgress()>=u.uLvl.getCurAttack().halfATime) {
                    attackStatus = 0;
                    u.attackAims();
                }
            }
            return attackStatus==3;
        }
        return true;
    }
}
