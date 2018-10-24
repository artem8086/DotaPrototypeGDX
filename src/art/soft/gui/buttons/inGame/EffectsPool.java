package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.gui.buttons.IButton;
import art.soft.gui.buttons.TButton;
import art.soft.spells.Effect;
import static art.soft.spells.Effect.BUFF_EFFECT;
import art.soft.units.Unit;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class EffectsPool extends TButton {

    private static final int alphaColor = 0x66000000;
    private static final int numP = 7;
    private static int[] x1 = {1,1,2,2,0,0,1}, y1 = {1,0,0,2,2,0,0}, x2 = new int[numP], y2 = new int[numP];

    public int dx, bx, by;
    private Effect sel;
    private Unit uSel;

    public String bBuff, bDeBuff;
    public Image im2, im3;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        dx *= xk; bx *= xk; by *= yk;
        if (bBuff!=null) {
            im2 = art.soft.utils.Image.loadImage(bBuff);
            bBuff = null;
        }
        if (bDeBuff!=null) {
            im3 = art.soft.utils.Image.loadImage(bDeBuff);
            bDeBuff = null;
        }
    }
    
    @Override
    public void draw(Graphics g){
        Unit u = Game.yourPlayer.gUnit.selUnit;
        if (u!=null && Game.yourPlayer.owner.groupInGroup(u.isVisible)) {
            active = true;
            uSel = u;
            Effect e = u.effects;
            int x = 0;
            while (e!=null) {
                if (e.Icon!=null && e.level>0 && (e.attrib & Effect.DONT_SHOW_EFF)==0) {
                    int cd = e.getCooldown();
                    if (cd>0 || !e.isDelete) {
                        drawEffect(g, x, e, cd);
                        x += width + dx;
                    }
                }
                e = e.next;
            }
        } else {
            active = false;
            uSel = null;
        }
    }

    private void drawCD(Graphics g, int cd, int ct, Image im, int x, int y, int width, int height){
        g.draw(im, x, y, width, height);
        if (ct!=0 && cd>0) {
            x += bx; y += by;
            width -= bx + bx;
            height -= by + by;
            g.setColor(alphaColor);
            float t = (1f - ((float) cd / ct)) * 4f + 0.5f;
            int n = (int) t;
            t -= n; n += 2;
            int tx = (int) (width * t);
            int ty = height - (int) (height * t);
            int wh = width >> 1;
            if (n>=numP) n = numP - 1;
            x2[n] = ((x1[n] * width) >> 1) + x;
            y2[n] = ((y1[n] * height) >> 1) + y;
            if (n==2) x2[n] += tx - width; else
            if (n==3) y2[n] -= ty; else
            if (n==4) x2[n] += width - tx; else
            if (n==5) y2[n] += ty; else
            if (n==6) x2[n] += tx - wh;
            for (int i=n-1; i>=0; i--) {
                x2[i] = ((x1[i] * width) >> 1) + x;
                y2[i] = ((y1[i] * height) >> 1) + y;
            }
            g.fillPolygon(x2, y2, n+1);
        }
    }

    public void drawEffect(Graphics g, int tx, Effect e, int cd) {
        int bw = width;
        int bh = height;
        int hw = bw >> 1;
        int hh = bh >> 1;
        drawCD(g, cd, e.getCooldownTime(), (e.attrib & BUFF_EFFECT)!=0 ? im2 : im3, x+tx-(hw>>1), y-(hh>>1), bw+hw, bh+hh);
        g.draw(e.Icon, x + tx, y, bw, bh);
        if (e.spell!=null && (e.attrib & Effect.SHOW_CHARGES)!=0) {
            String s = "" + e.spell.number;
            g.setFont(font);
            g.setColor(color);
            int w = g.stringWidth(s);
            g.drawString(s, x + tx - w - 1 + bw, y - g.fontCapHeight() - 1 + bh);
        }
    }

    @Override
    public IButton onGUI(int x, int y){
        if (active && uSel!=null) {
            Effect e = uSel.effects;
            int tx = 0;
            while (e!=null) {
                if (e.Icon!=null && !e.isDelete && e.level>0) {
                    if (contains(x - tx, y)) {
                        sel = e;
                        return this;
                    }
                    tx += width + dx;
                }
                e = e.next;
            }
            return null;
        } else 
            return null;
    }

    @Override
    public void dispose(){
        super.dispose();
        if (im2!=null) im2.dispose();
        if (im3!=null) im3.dispose();
    }
}
