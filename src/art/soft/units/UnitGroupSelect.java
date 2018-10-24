package art.soft.units;

import art.soft.Game;
import art.soft.gui.buttons.inGame.GroupUnit;
import art.soft.multiplayer.*;
import art.soft.utils.Rectangle;
import static art.soft.units.Unit.IS_SELECT;
import static art.soft.units.Unit.NOT_SELECTABLE;
import art.soft.units.actions.useItemAlready;

/**
 *
 * @author Артем
 */
public class UnitGroupSelect extends UnitGroup {

public static boolean selectPos, selectRect;
public Unit selUnit;
public int selNum;
private static UnitGroup ug, u;
private static Rectangle r = new Rectangle();
private static Unit op;

public void remove(){
    UnitGroup p = this;
    UnitGroup n;
    if (unit!=null)
    while (p!=null) {
        if (p.unit!=null) p.unit.status &= ~IS_SELECT;
        n = p.next;
        UnitGroup.poolGroup(p);
        p = n;
    }
}

public void getUnitsFromVISRect (Rectangle r) {
    op = null;
    this.r.setBounds(r);
    if (r.width==0) {
        selectPos = true;
    } else {
        u = null;
        ug = null;
        selectRect = true;
    }
}

public void select (Unit p) {
    //if (selectPos && op==null) {
        //if( p.rect.contains( r.x, r.y ) ) op = p;
    //} else
    if ((p.status & NOT_SELECTABLE)==0)
    if (selectRect) {
        if (r.contains(p.px, p.py)) {
            if (op==null) op = p;
            //if( p.ownerID==owner ){
            if (p.owners.inGroup(Game.yourPlayerID)) {
                if( u==null ){
                    remove();
                    u = ug = getGroup();
                } else {
                    if (p==Game.yourPlayer.mainUnit) {
                        p.select();
                        Unit d = u.unit;
                        u.unit = p;
                        p = d;
                    }
                    ug.next = getGroup();
                    ug = ug.next;
                }
                p.select();
                ug.unit = p;
            }
        }
    }
}

public boolean slectEnd(){
    if (selectPos) {
        selectPos = false;
        if (Game.unitSelectNow!=null) {
            remove();
            selUnit = unit = Game.unitSelectNow;
            unit.select();
            GroupUnit.page = 0;
            selNum = 0;
            return true;
        }
    }
    if (selectRect) {
        selectRect = false;
        if (u!=null) {
            selUnit = unit = u.unit;
            next = u.next;
            GroupUnit.page = 0;
            selNum = 0;
            return true;
        } else if( op!=null ){
            remove();
            selUnit = unit = op;
            op.select();
            GroupUnit.page = 0;
            selNum = 0;
            return true;
        }
    }
    //selUnit = unit;
    //selNum = 0;
    return false;
}

public void selectUnit(Unit u, int owner) {
    if (owner==Server.playerID) remove();
    else removeGrp();
    if( u!=null && u.owners.youInGroup() ) u.select();
    selUnit = unit = u;
    GroupUnit.page = 0;
    selNum = 0;
}

public void selectGroup( UnitGroup u ){
    remove();
    unit = u.unit;
    next = u.next;
    if( unit!=null ) unit.select();
    u = next;
    while( u!=null ){
        u.unit.select();
        u = u.next;
    }
    selUnit = unit;
    GroupUnit.page = 0;
    selNum = 0;
}

public void setMovePos( int x, int y, Player owner, boolean add ){
    if (unit==null || !owner.owner.groupInGroup(unit.canControl) || (unit.status & Unit.CANT_MOVE)!=0)
        selectUnit( owner.mainUnit, owner.ID );

    UnitGroup ug = this;
    if (unit!=null)
    while (ug!=null) {
        if (ug.unit!=null && owner.owner.groupInGroup(ug.unit.canControl)) ug.unit.setMovePos( x, y, add );
        ug = ug.next;
    }
}

public void setMoveToUnit(Unit u, Player owner, boolean add) {
    if (unit==null || !owner.owner.groupInGroup(unit.canControl) || (unit.status & Unit.CANT_MOVE)!=0)
        selectUnit( owner.mainUnit, owner.ID );

    UnitGroup ug = this;
    if( unit!=null )
    while( ug!=null ){
        if (owner.owner.groupInGroup( ug.unit.canControl )) ug.unit.setMoveToUnit( u, add );
        ug = ug.next;
    }
}

public void setAttackUnit( Unit u, Player owner, boolean add ){
    if( unit==null || !owner.owner.groupInGroup( unit.canControl ) )
        selectUnit( owner.mainUnit, owner.ID );

    UnitGroup ug = this;
    if( unit!=null )
    while( ug!=null ){
        if (owner.owner.groupInGroup( ug.unit.canControl )) ug.unit.setAttackUnit( u, true, add );
        ug = ug.next;
    }
}

public void setAttackPos( int x, int y, Player owner, boolean add ){
    if( unit==null || !owner.owner.groupInGroup( unit.canControl ) )
        selectUnit( owner.mainUnit, owner.ID );

    UnitGroup ug = this;
    if( unit!=null )
    while( ug!=null ){
        if (owner.owner.groupInGroup( ug.unit.canControl )) ug.unit.setAttackPos( x, y, add );
        ug = ug.next;
    }
}

public void learnStats(Player owner){
    if (selUnit!=null && selUnit.attribSpell!=null && owner.owner.groupInGroup( selUnit.canControl )) {
        selUnit.attribSpell.incSpellLevel(selUnit);
    } 
}

public void learnAbility(int skill, Player owner){
    if (selUnit!=null &&  selUnit.spells!=null && owner.owner.groupInGroup( selUnit.canControl )) {
        selUnit.spells[skill+selUnit.startS].incSpellLevel(selUnit);
    } 
}

public void autoCastON(int skill, Player owner){
    if (selUnit!=null && selUnit.spells!=null && owner.owner.groupInGroup( selUnit.canControl )) {
        selUnit.spells[skill+selUnit.startS].isAutoCastOn = true;
    } 
}

public void autoCastOFF(int skill, Player owner){
    if (selUnit!=null && selUnit.spells!=null && owner.owner.groupInGroup(selUnit.canControl)) {
        selUnit.spells[skill+selUnit.startS].isAutoCastOn = false;
    } 
}

public void castSpellOnUnit(int skill, Unit aim, Player owner, boolean add){
    if (selUnit!=null && selUnit.spells!=null && owner.owner.groupInGroup(selUnit.canControl)) {
        selUnit.castSpellToUnit(aim, selUnit.spells[skill+selUnit.startS], add);
    }
}

public void castSpellToPos(int skill, int x, int y, Player owner, boolean add){
    if (selUnit!=null && selUnit.spells!=null && owner.owner.groupInGroup(selUnit.canControl)) {
        selUnit.castSpellToPos(x, y, selUnit.spells[skill+selUnit.startS], add);
    }
}

public void castSpellAlready(int skill, Player owner, boolean add){
    if (selUnit!=null && selUnit.spells!=null && owner.owner.groupInGroup( selUnit.canControl )) {
        selUnit.castSpellToUnit(selUnit, selUnit.spells[skill+selUnit.startS], add);
    }
}

public void useItemOnUnit(int slot, Unit aim, Player owner, boolean add){
    if (selUnit!=null && selUnit.spells!=null && owner.owner.groupInGroup( selUnit.canControl )) {
        selUnit.useItemOnUnit(slot, aim, add);
    }
}

public void useItemToPos(int slot, int x, int y, Player owner, boolean add){
    if (selUnit!=null && selUnit.spells!=null && owner.owner.groupInGroup( selUnit.canControl )) {
        selUnit.useItemToPos(slot, x, y, add);
    }
}

public void useItemAlready(int slot, Player owner, boolean add){
    if (selUnit!=null && selUnit.spells!=null && owner.owner.groupInGroup( selUnit.canControl )) {
        useItemAlready act = Action.setAction(selUnit, useItemAlready.class, add);
        if (act!=null) act.setItem(slot);
    }
}

public void dropItemToUnit(int item, Unit aim, Player owner, boolean add){
    if (selUnit!=null && owner.owner.groupInGroup( selUnit.canControl )) {
        selUnit.dropItemToUnit(item, aim, add);
    }
}

public void dropItemToPos(int item, int x, int y, Player owner, boolean add){
    if (selUnit!=null && owner.owner.groupInGroup( selUnit.canControl )) {
        selUnit.dropItemToPos(item, x, y, add);
    }
}

public void changeItemSlot(int item1, int item2, Player owner) {
    if (selUnit!=null && owner.owner.groupInGroup( selUnit.canControl )) {
        selUnit.changeItemSlot(item1, item2);
    }
}

public void selectUnitFormGroup(int uNum) {
    UnitGroup u = this;
    selNum = 0;
    selUnit = unit;
    int n = uNum;
    while (u!=null) {
        if (n==0) {
            selNum = uNum;
            selUnit = u.unit;
            return;
        }
        n --;
        u = u.next;
    }
}

}
