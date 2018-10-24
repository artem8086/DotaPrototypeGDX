package art.soft.specal;

import art.soft.Graphics;
import art.soft.units.AnimData;

/**
 *
 * @author Artem
 */
public class Simple extends specEffect {

    public int color;
    
    public void Init(AnimData aData, int x, int y, int col, float size, int num, int vis){
        super.Init(x, y, aData, vis);
        setZoom(size);
        setAnim(num);
        isCycle = false;
        color = col;
    }

    @Override
    public boolean incAnim(){
        return incAnm();
    }

    @Override
    public boolean move() { return false; }

    @Override
    public void play( Graphics g, int x, int y ){
        g.setAlpha(color);
        super.play(g, x, y);
        g.setAlphaNormal();
    }
}
