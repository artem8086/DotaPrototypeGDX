package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.gui.buttons.TButton;
import static art.soft.gui.Interface.MOUSE_OVER;
import art.soft.units.ULevelHero;
import art.soft.units.Unit;
import com.badlogic.gdx.graphics.Color;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class HeroAttributes extends TButton{

    private int m_str, m_agl, m_int, t_str, t_agl, t_int;
    
    public String border;
    public Image im2;
    public String lvlUp, atrUp;
    public static Image borderUP, statsUP;
    public int keyD;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        if (border!=null) {
            im2 = art.soft.utils.Image.loadImage(border);
            border = null;
        }
        if (lvlUp!=null) {
            borderUP = art.soft.utils.Image.loadImage(lvlUp);
            lvlUp = null;
        }
        if (atrUp!=null) {
            statsUP = art.soft.utils.Image.loadImage(atrUp);
            atrUp = null;
        }
        keyD = key;
    }

    @Override
    public void draw(Graphics g){
        Unit u = Game.yourPlayer.gUnit.selUnit;
        key = 0;
        if (Game.yourPlayer.gUnit.next==null && u!=null && u.uLvl!=null && u.uLvl instanceof ULevelHero) {
            active = true;
            g.setFont(font);
            Image i = UnitAttributes.attribIM;
            g.draw(i, x, y, width, height);
            im = null;
            if (Game.yourPlayer.owner.groupInGroup(u.canControl)) {
                if (Game.statusKey==Game.DO_LEVEL_UP && u.attribSpell!=null && u.attribSpell.canUpgradeSpell(u)!=0) {
                    im = statsUP;
                    key = keyD;
                }
            }
            int m = u.uData.main_atr;
            if (Game.yourPlayer.owner.groupInGroup(u.isVisible)) {
                ULevelHero uLvl = (ULevelHero) u.uLvl;
                m_str = (int) uLvl.m_str; t_str = (int) uLvl.t_str;
                m_agl = (int) uLvl.m_agl; t_agl = (int) uLvl.t_agl;
                m_int = (int) uLvl.m_str; t_int = (int) uLvl.t_int;
            }
            super.draw(g);
            drawAttrib(g, m_str, t_str, buttons[0], m==0);
            drawAttrib(g, m_agl, t_agl, buttons[1], m==1);
            drawAttrib(g, m_int, t_int, buttons[2], m==2);
            im = i;
        } else
            active = false;
    }

    public void drawAttrib(Graphics g, int m, int t, IButton but, boolean main){
        int bw = but.width;
        int bh = but.height;
        int hh = bh >> 1;
        int x = but.x;
        int y = but.y;
        if (main) {
            int hw = bw >> 1;
            g.draw(im2, x-(hw>>1), y-(hh>>1), bw+hw, bh+hh);
        }
        g.setColor(color);
        String s = "  " + m;
        int h = (g.fontAscent() - (g.fontHeight()>>1));
        int w = g.stringWidth(s);
        g.drawString(s, x + bw, y + h + hh);
        if (t!=0) {
            if (t>0) {
                g.setColor(Color.GREEN);
                s = "+" + t;
            } else {
                g.setColor(Color.RED);
                s = "" + t;
            }
            g.drawString(s, x + w + bw, y + h + hh);
        }
    }

    @Override
    public IButton onGUI(int x, int y){
        if (active) {
            //if (thisKeyPressed()) return this;
            if (buttons!=null && !Game.mbut1) {
                IButton b;
                for (IButton but : buttons) {
                    b = but.onGUI(x, y);
                    if (b!=null) return b;
                }
            }
            return contains(x, y) ? this : null;
        } else 
            return null;
    }

    @Override
    public void pressed(int code, int k){
        if (code==Interface.BUTTON_PRESSED) {
            if (Game.statusKey==Game.DO_LEVEL_UP) {
                Unit u = Game.yourPlayer.gUnit.selUnit;
                if (Game.yourPlayer.owner.groupInGroup(u.canControl)) {
                    Game.CShost.playerLearnStats();
                    //u.attribSpell.incSpellLevel(u);
                    //if ((u.uLvl.level - u.uLvl.old_level)>0) Game.levelUp = true;
                } else
                    Game.statusKey = Game.DO_NOTHING;
            } else Game.statusKey = Game.DO_NOTHING;
        } else
        if (code!=MOUSE_OVER && code!=Interface.BUTTON_RELESSED)
            Game.statusKey = Game.DO_NOTHING;
    }

    @Override
    public void dispose(){
        super.dispose();
        if (im2!=null) im2.dispose();
        if (borderUP!=null) { borderUP.dispose(); borderUP = null; }
        if (statsUP!=null) { statsUP.dispose(); statsUP = null; }
    }
}

