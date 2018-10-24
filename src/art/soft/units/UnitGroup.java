package art.soft.units;

import art.soft.*;
import art.soft.utils.Rectangle;
/**
 *
 * @author Артем
 */
public class UnitGroup {

public static final int MAX_UNIT_IN_GROUP = 60;

            // Пул для неиспользуемых групп юнитов
public static UnitGroup poolGroup, lastGroup;

public Unit unit;
public UnitGroup next;

public UnitGroup(){};

public static UnitGroup getGroup(){
    if (poolGroup!=null) {
        UnitGroup g = poolGroup;
        poolGroup = poolGroup.next;
        if (poolGroup==null) lastGroup = null;
        return g;
    } else
        return new UnitGroup();
}

public static UnitGroup getGroup( Unit u ){
    UnitGroup g = getGroup();
    g.unit = u;
    return g;
}

public void removeGrp(){
    UnitGroup p = this;
    UnitGroup n;
    //if (unit!=null)
    while (p!=null) {
        n = p.next;
        poolGroup(p);
        p = n;
    }
}

public static void poolGroup( UnitGroup g ){
    if (!(g instanceof UnitGroupSelect)) {
        if( poolGroup==null ){
            lastGroup = poolGroup = g;
        } else {
            lastGroup.next = g;
            lastGroup = g;
        }
    }
    g.unit = null;
    g.next = null;
}
public void getUnitsFromRect(Rectangle r) {
    removeGrp();
    UnitGroup ug = this;
    Unit p = (Unit) Game.allUnits.first;
    UnitGroup prev = this;
    while( p!=null ){
        if( r.contains( p.px, p.py ) ){
            prev = ug;
            ug.unit = p;
            ug.next = getGroup();
            ug = ug.next;
        }
        p = (Unit) p.next;
    }
    prev.next = null;
}

public void setGroup( UnitGroup u ){
    removeGrp();
    unit = u.unit;
    next = u.next;
}

public void getGroupFromData( byte[] data, int ofs ){
    removeGrp();
    int num = data[ ofs++ ] & 255;
    if( num!=0 ){
        UnitGroup ug = this;
        UnitGroup prev = this;
        for( ; num>0; num-- ){
            Unit u = Game.getUnitByID( (data[ ofs ] & 255) | ((data[ ofs+1 ] & 255)<<8) );
            if( u!=null ){
                prev = ug;
                ug.unit = u;
                ug.next = getGroup();
                ug = ug.next;
            }
            ofs += 2;
        }
        prev.next = null;
    }
}

public void setGroupToData( byte[] data, int ofs ){
    int num = 0;
    UnitGroup ug = this;
    int a = ofs;
    ofs = a + 1;
    while( ug!=null ){
        if( ug.unit!=null ){
            int id = ug.unit.ID;
            data[ ofs++ ] = (byte) id;
            data[ ofs++ ] = (byte) (id >> 8);
            num ++;
            if( num>=MAX_UNIT_IN_GROUP ) break;
        }
        ug = ug.next;
    }
    data[ a ] = (byte) num;
}

}