package art.soft.map;

import art.soft.*;
import art.soft.units.*;

/**
 *
 * @author Артем
 */
public class Decoration extends Animation {

    public int cType; // тип коллизии
    public int d; // диаметр в ячейках
    public int h; // высота в пикселях (используется для расчёта видимости)
    public String anim;

    public Decoration(){}

    public Decoration( AnimData aData, int ct, int d, int h ){
        super( aData );
        Init();
        this.d = d;
        this.h = h;
        cType = ct;
    }

    public void Load() {
        if (anim!=null) {
            aData = AnimData.load(anim);
            anim = null;
        }
    }

    public final void Init() {
        setAnim(cur_anim < 0 ? 0 : cur_anim);
        setDir(cur_dir < 0 ? 0 : cur_dir);
    }

    public boolean setDecoration( int x, int y, int ID ){
        GameMap m = Game.map;
        int a = (y<<m.ssz)+x;
        if( (m.map[a] & 0xC0)==0 ){
            if( cType!=0 ){
                x -= d>>1;
                y -= d-1;
                if( m.getCollRectMapMask( x, y, d, cType ) ) return false;
                y -= m.setRectMapMask( x, y, d, d, cType );
            }
            m.map[a] &= 0xFF3F;
            m.map[a] |= (ID << 16) | 0x40;
            return true;
        }
        return false;
    }

    public void removeDecoration( int x, int y ){
        GameMap m = Game.map;
        int a = (y<<m.ssz)+x;
        if( (m.map[a] & 0x70)!=0 ){
            if( cType!=0 ){
                x -= d>>1;
                y -= d-1;
                m.clearRectMapMask( x, y, d, d, cType );
            }
            m.map[a] &= 0xFF8F;
        }
    }

}
