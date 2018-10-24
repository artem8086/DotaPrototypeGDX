package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.buttons.IButton;
import art.soft.units.Unit;
import static art.soft.units.Unit.IS_ALIVE;
import art.soft.utils.Image;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 *
 * @author Артем
 */
public class HP_MP_bar extends IButton {

    public String HP_bar, MP_bar, spec;
    public Image HP, MP, im2;
    public int fontSize, color, shiftX;
    public boolean bold;
    public BitmapFont font1, font2;
    
    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        shiftX *= xk;
        if (HP_bar!=null) {
            HP = art.soft.utils.Image.loadImage(HP_bar);
            HP_bar = null;
        }
        if (MP_bar!=null) {
            MP = art.soft.utils.Image.loadImage(MP_bar);
            MP_bar = null;
        }
        if (spec!=null) {
            im2 = art.soft.utils.Image.loadImage(spec);
            spec = null;
        }
        font1 = Game.getFont(fontSize, bold);
        font2 = Game.getFont(fontSize-2, bold);
    }
    
    @Override
    public void draw(art.soft.Graphics g){
        Unit u = Game.yourPlayer.gUnit.selUnit;
        if (u!=null && u.uLvl!=null) {
            if ((u.status & IS_ALIVE)!=0){
                g.setColor(color);
                g.setFont(font1);
                int fh = (g.fontAscent() - (g.fontHeight()>>1));
                String s;
                int y = this.y;
                //int mp = 
                int h = height * 2 / 3;
                int hp = u.uLvl.getCurHP();
                int hpp = u.uLvl.max_hp;
                if (hp==hpp) {
                    g.draw(HP, x, y, width, h);
                } else {
                    float dw = (float) hp / hpp;
                    int w = (int) (width * dw);
                    g.draw(HP, x, y, w, h,
                            0, 0, (int) (HP.getWidth() * dw), HP.getHeight());
                    g.draw(im2, x+w-h+shiftX, y-(h >> 1), h, h+h);
                    float rhp = u.uLvl.hpReg;
                    s = String.format("%.1f", rhp);
                    if (rhp>=0) s = "+" + s;
                    w = g.stringWidth(s);
                    g.drawString(s, x - w + width - shiftX, y + fh + (h>>1));
                }
                s = ""+hp+"/"+hpp;
                int w = g.stringWidth(s) >> 1;
                g.drawString(s, x - w + (width>>1), y + fh + (h>>1));

                int mpp = u.uLvl.max_mp;
                int mp = 0;
                g.setFont(font2);
                y += h + 2;
                h = height / 3;
                if (mpp!=0) {
                    mp = u.uLvl.getCurMana();
                    if (mp==mpp) {
                        g.draw(MP, x, y, width, h);
                    } else {
                        float dw = (float) mp / mpp;
                        w = (int) (width * dw);
                        g.draw(MP, x, y, w, h,
                                0, 0, (int) (MP.getWidth() * dw), MP.getHeight());
                        g.draw(im2, x+w-h+2, y-(h >> 1), h, h+h);
                        float rmp = u.uLvl.mpReg;
                        s = String.format("%.1f", rmp);
                        if (rmp>=0) s = "+" + s;
                        w = g.stringWidth(s);
                        g.drawString(s, x - w + width - 2, y + fh + (h>>1));
                    }
                }
                s = ""+mp+"/"+mpp;
                w = g.stringWidth(s) >> 1;
                g.drawString(s, x - w + (width>>1), y + fh + (h>>1));
            } else {
                super.draw(g);
            }
        }
    }

    @Override
    public void dispose(){
        super.dispose();
        if (HP!=null) HP.dispose();
        if (MP!=null) MP.dispose();
        if (im2!=null) im2.dispose();
    }
}
