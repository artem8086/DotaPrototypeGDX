package art.soft.gui.buttons.inGame;

import art.soft.Game;
import static art.soft.Game.CShost;
import art.soft.Graphics;
import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.gui.buttons.TButton;
import static art.soft.gui.buttons.inGame.AbilityPool.dx;
import static art.soft.gui.buttons.inGame.AbilityPool.xb;
import art.soft.spells.Effect;
import art.soft.spells.Spell;
import art.soft.units.Unit;
import static art.soft.units.Unit.CANT_USE_SPELLS;
import art.soft.utils.Image;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 *
 * @author Артем
 */
public class Ability extends TButton {

    public static final int alphaColor = 0x55000000;
    public static final int numP = 7;
    public static int[] x1 = {1,1,0,0,2,2,1}, y1 = {1,0,0,2,2,0,0}, x2 = new int[numP], y2 = new int[numP];
    private static Image[] cycleIM = new Image[8];

    public int sNum, sUp, fontSize2;
    public Spell spell;
    public String borderP, mana, im1, im2, im3, im4, im5, im6, im7, cycle;
    public static Image bPASSIVE, manaP, img1, img2, img3, img4, img5, img6, img7;
    public boolean canCast, onBut;

    public static BitmapFont font2;
    public int margin, s4, s5, s6, wm, hm, keyD;
    public static int mrg, wh4, wh5, wh6, wM, hM;
    
    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        mrg = (int) (margin * xk);
        wh4 = (int) (s4 * xk);
        wh5 = (int) (s5 * xk);
        wh6 = (int) (s6 * xk);
        wM = (int) (wm * xk);
        hM = (int) (hm * xk);
        keyD = key;
        if (fontSize2!=0)
            font2 = Game.getFont(fontSize2, bold);
        if (borderP!=null) {
            bPASSIVE = art.soft.utils.Image.loadImage(borderP);
            borderP = null;
        }
        if (mana!=null) {
            manaP = art.soft.utils.Image.loadImage(mana);
            mana = null;
        }
        if (im1!=null) {
            img1 = art.soft.utils.Image.loadImage(im1);
            im1 = null;
        }
        if (im2!=null) {
            img2 = art.soft.utils.Image.loadImage(im2);
            im2 = null;
        }
        if (im3!=null) {
            img3 = art.soft.utils.Image.loadImage(im3);
            im3 = null;
        }
        if (im4!=null) {
            img4 = art.soft.utils.Image.loadImage(im4);
            im4 = null;
        }
        if (im5!=null) {
            img5 = art.soft.utils.Image.loadImage(im5);
            im5 = null;
        }
        if (im6!=null) {
            img6 = art.soft.utils.Image.loadImage(im6);
            im6 = null;
        }
        if (im7!=null) {
            img7 = art.soft.utils.Image.loadImage(im7);
            im7 = null;
        }
        if (cycle!=null)
        for (int i=7; i>=0; i--)
            cycleIM[i] = art.soft.utils.Image.loadImage(cycle + i);
    }

    @Override
    public void draw(art.soft.Graphics g){
        key = 0;
        if (spell!=null) {
            active = true;
            text = null;
            Image i = spell.getIcon();
            int tx = x;
            xb += dx;
            x += xb;
            if (i==null) i = AbilityPool.eAbility;
            boolean activeSp = (spell.attrib & Spell.IS_ACTIVE_SPELL)!=0;
            boolean autoCast = (spell.attrib & Spell.AUTO_SPELL_CAST)!=0;
            int lvl = spell.getSpellLevel();
            Effect e = spell.effect;
            boolean haveMana = e!=null ? e.haveMana() : true;
            int cd = spell.getCooldown();
            if (activeSp || autoCast) {
                im = null;
                int wh = (int) (width / 4.5f);
                int hh = (int) (height / 4.5f);
                int x1 = x - (wh >> 1);
                int y1 = y - (hh >> 1);
                int w1 = width + wh;
                int h1 = height + hh;
                if (activeSp) {
                    if ((spell.attrib & Spell.AUTO_SPELL_CAST)!=0) {
                        g.draw(img7, x1, y1, w1, h1);
                        if (spell.isAutoCastOn)
                            g.draw(cycleIM[AbilityPool.cyct >> 7], x1, y1, w1, h1);
                    } else {
                        g.draw(img4, x1, y1, w1, h1);
                        if (haveMana && lvl>0) {
                            g.draw(img5, x1, y1, w1, h1);
                        }
                    }
                    if (haveMana && lvl>0 && cd<=0 && canCast) key = keyD;
                }
            } else im = bPASSIVE;
            if (lvl<=0 || !haveMana || !canCast || cd>0) {
                g.setGray(true);
                g.draw(i, x, y, width, height);
                g.setGray(false);
            } else {
                //active = true;
                g.draw(i, x, y, width, height);
            }
            if (onBut) g.draw(img6, x, y, width, height);
            if (cd>0) {
                drawCD(g, this, cd, spell.getCooldownTime());
                int c = cd / 1000;
                if (c!=0) setText("" + c);
                else setText("0." + (cd / 100));
            }
            if (Game.statusKey==Game.DO_LEVEL_UP) {
                if (sUp>0) {    
                    key = keyD;
                    im = HeroAttributes.borderUP;
                } else key = 0;
            }
            super.draw(g);
            int m = spell.getManacost();
            if (m>0 && lvl>0 && (sUp<=0 || Game.statusKey!=Game.DO_LEVEL_UP)) {
                g.draw(manaP, x+width-wM+2, y+height-hM, wM, hM);
                g.setFont(font2);
                g.setColor(color);
                String s = "" + m;
                int w = g.stringWidth(s);
                g.drawString(s, x - w - 2 + width, y - g.fontCapHeight() + height);
            }
            drawAbilityLevel(g, x+(width>>1), y+height+mrg, lvl, spell.maxUpgrade);
            x = tx;
        } else
            active = false;
        onBut = false;
    }

    public static void drawCD(Graphics g, IButton b, int cd, int ct){
        if (ct!=0) {
            g.setColor(alphaColor);
            int x = b.x;
            int y = b.y;
            int width = b.width;
            int height = b.height;
            float t = ((float) cd / ct) * 4f + 0.5f;
            int n = (int) t;
            t -= n; n += 2;
            int tx = (int) (width * t);
            int ty = height - (int) (height * t);
            int wh = width >> 1;
            if (n>=numP) n = numP - 1;
            x2[n] = ((x1[n] * width) >> 1) + x;
            y2[n] = ((y1[n] * height) >> 1) + y;
            if (n==2) x2[n] -= tx - width; else
            if (n==3) y2[n] -= ty; else
            if (n==4) x2[n] -= width - tx; else
            if (n==5) y2[n] += ty; else
            if (n==6) x2[n] += wh - tx;
            for (int i=n-1; i>=0; i--) {
                x2[i] = ((x1[i] * width) >> 1) + x;
                y2[i] = ((y1[i] * height) >> 1) + y;
            }
            g.fillPolygon(x2, y2, n+1);
        }
    }

    private void drawAbilityLevel(Graphics g, int x, int y, int lvl, int ml){
        int mc = lvl + ml;
        int w = wh4; int ns = mc;
        if (mc==5) w = wh5; else
        if (mc>5) { w = wh6; ns = 6; }
        int dx = (w * ns) >> 1;
        int tx = x - dx;
        for (int i=0; i<mc; i++) {
            ns --;
            Image im = i < lvl ? img1 : (Game.statusKey==Game.DO_LEVEL_UP && i < (lvl + sUp) ? img2 : img3);
            g.draw(im, tx, y, w, w);
            tx += w;
            if (ns==0) {
                ns = 6;
                tx = x - dx;
                y += w;
            }
        }
    }

    @Override
    public IButton onGUI(int x, int y){
        if (active) {
            xb += dx;
            if (contains(x - xb, y)) return this;
        }
        return null;
    }

    @Override
    public void pressed(int code, int k){
        if (code==Interface.MOUSE_OVER) {
            if (spell!=null && spell.getSpellLevel()>0) {
                if (Game.statusKey!=Game.DO_LEVEL_UP && spell.radius>0) {
                    Unit u = spell.getSpellOwner();
                    if (u!=null) {
                        Game.circleR = spell.radius;
                        Game.circleX = u.px;
                        Game.circleY = u.py;
                    }
                }
            }
        } else
        if ((spell.getSpellOwner().status & CANT_USE_SPELLS)==0) {
            if (code==Interface.ALTERNATIVE_PRESSED || (Game.alt && code==Interface.BUTTON_PRESSED)) {
                if (Game.statusKey!=Game.DO_LEVEL_UP && (spell.attrib & Spell.AUTO_SPELL_CAST)!=0) {
                    if (spell.isAutoCastOn) Game.CShost.playerAutoCastOFF(sNum);
                    else Game.CShost.playerAutoCastON(sNum);
                    //System.out.println("Autocast[Spell:"+spell.Name+"] = "+spell.isAutoCastOn);
                }
                Game.statusKey = Game.DO_NOTHING;
            } else
            if (code==Interface.BUTTON_PRESSED) {
                if (Game.statusKey==Game.DO_LEVEL_UP) {
                    if (spell!=null && sUp>0) {
                        Game.CShost.playerLearnAbility(sNum);
                    } else
                        Game.statusKey = Game.DO_NOTHING;
                } else if (key!=0) {
                    if ((spell.attrib & Spell.CAST_ALREADY)!=0) {
                        CShost.playerCastAlready(sNum);
                    } else {
                        Game.statusKey = Game.DO_SELECT_SPELL;
                        Game.castSpell = spell;
                        Game.spellNum = sNum;
                    }
                } else
                    Game.statusKey = Game.DO_NOTHING;
            } else if (code!=Interface.BUTTON_RELESSED)
                Game.statusKey = Game.DO_NOTHING;
        } else Game.statusKey = Game.DO_NOTHING;
        onBut = true;
    }

    @Override
    public void dispose(){
        super.dispose();
        if (img1!=null) { img1.dispose(); img1 = null; }
        if (img2!=null) { img2.dispose(); img2 = null; }
        if (img3!=null) { img3.dispose(); img3 = null; }
        if (img4!=null) { img4.dispose(); img4 = null; }
        if (img5!=null) { img5.dispose(); img5 = null; }
        if (img6!=null) { img6.dispose(); img6 = null; }
        if (img7!=null) { img7.dispose(); img7 = null; }
        for (int i=cycleIM.length-1; i>=0; i --) {
            if (cycleIM[i]!=null) { cycleIM[i].dispose(); cycleIM[i] = null; }
        }
        if (bPASSIVE!=null) { bPASSIVE.dispose(); bPASSIVE = null; }
        if (manaP!=null) { manaP.dispose(); manaP = null; }
    }
}
