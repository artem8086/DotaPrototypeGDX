package art.soft.units;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.utils.StackObj;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class Animation extends StackObj {

    public AnimData aData;
    private Image CurAnim;
    private short[] curADW;
    public int cur_anim = -1, cur_dir;
    private int len;
    private float dTime, pTime, num;
    public float k = 1f; // k - коэфециэнт масштабирования
    public boolean isCycle = true;

    public Animation() {}

    public Animation(AnimData aData) {
        this.aData = aData;
    }

    public void setData(AnimData aData) {
        this.aData = aData;
        reset();
    }

    public void reset() {
        curADW = null;
        CurAnim = null;
        cur_anim = -1;
    }

    public void setAnim(int num, float time) {
        if (num>=aData.Data.length) num = 0;
        if (cur_anim!=num) {
            cur_anim = num;
            setDir( cur_dir );
            CurAnim = aData.Data[ num ];
            curADW = aData.Sdat[ num ][ cur_dir ];
            len = curADW.length >> 3;
            pTime = (float) ((int) aData.Time[num] & 0xFFFF) * 0.001f;
            this.num = 0;
            setTime( time );
        } else
            setTime( time );
    }

        // Not Safe Metod
    public void setFastAnim_(int num) {
        if (cur_anim!=num) {
            cur_anim = num;
            CurAnim = aData.Data[ num ];
            curADW = aData.Sdat[ num ][ cur_dir ];
        }
    }

    public void setAnimNoAnim(int num, int dir) {
        if (num>=aData.Data.length) num = 0;
        if (cur_anim!=num) {
            cur_anim = num;
            setDir( dir );
            CurAnim = aData.Data[ num ];
            curADW = aData.Sdat[ num ][ cur_dir ];
            len = curADW.length >> 3;
            //h = 1; dx = 1;
            this.num = 0;
        }
    }

    public void setAnim(int num) {
        setAnim( num, 1 );
    }

    public void setDir(int dir) {
        if( aData.Sdat[ cur_anim ].length<=dir ) dir = 0;
        cur_dir = dir;
        curADW = aData.Sdat[ cur_anim ][ dir ];
    }

    public float getProgress(){
        return num / len;
    }

    public void setAngle(int angle) {
        int n = aData.Sdat[ cur_anim ].length;
        if( n>1 ){
            int dAngle = 360 / n;
            angle += dAngle >> 1;
            if( angle<0 ) angle += 360;
            setDir( angle / dAngle );
        } else {
            setDir( 0 );
        }
    }

    public void setTime(float dTime) {
        this.dTime = len / (dTime * pTime);
        //System.out.println( "pTime = "+pTime+"; tTime = "+(tTime)+"; dTime = "+(dTime) );
    }

    public void play(Graphics g, int x, int y) {
        int n = ((int) num) << 3;
        if (n<0) n = 0;
        int x1 = curADW[n++];
        int y1 = curADW[n++];
        int x2 = curADW[n++];
        int y2 = curADW[n++];
        int ox = (int) (curADW[n++] * k);
        int oy = (int) (curADW[n] * k);
        int w = (int) ((x2 - x1) * k);
        int h = (int) ((y2 - y1) * k);
        x -= ox;
        y -= h - oy;
        g.draw(CurAnim, x, y, w, h, x1, y1, x2, y2);
    }

    public void play(Graphics g, int x, int y, float angle) {
        int n = ((int) num) << 3;
        if (n<0) n = 0;
        int x1 = curADW[n++];
        int y1 = curADW[n++];
        int x2 = curADW[n++];
        int y2 = curADW[n++];
        int ox = (int) (curADW[n++] * k);
        int oy = (int) (curADW[n] * k);
        int w = (int) ((x2 - x1) * k);
        int h = (int) ((y2 - y1) * k);
        x -= ox;
        y -= h - oy;
        g.draw(CurAnim, x, y, w, h, x1, y1, x2, y2, ox, oy, - angle);
    }

    public int getDopX() {
        return (int) (curADW[6] * k);
    }

    public int getDopY() {
        return (int) (curADW[7] * k);
    }

    public void setZoom(float zoom) {
        k = zoom;
    }

    public float getZoom() {
        return k;
    }

    public void restart() {
        num = 0;
    }

    public boolean incAnm() {
        num += dTime * Game.deltaT;
        if( num>=len ){
            if( isCycle ) num -= (int) num;
            else num = len-1;
            return true;
        }
        return false;
    }
}
