package art.soft.gui.buttons.inGame;

import art.soft.Game;
import static art.soft.Game.CShost;
import static art.soft.Game.CURSOR_SPELL_WALK_TO;
import static art.soft.Game.curCur;
import art.soft.gui.Interface;
import art.soft.gui.buttons.DragTarget;
import art.soft.gui.buttons.DraggedBut;
import art.soft.gui.buttons.IButton;
import art.soft.spells.Spell;
import art.soft.units.Unit;
import com.badlogic.gdx.graphics.Color;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class UnitIcon extends IButton implements DragTarget {

    public Unit unit;
    public int numUnit;
    public int x1, y1, w1, h1;
    private static int dx1, dy1, dw1, dh1;
    public int x2, y2, w2, h2;
    private static int dx2, dy2, dw2, dh2;
    public int y3, h3;
    private static int dy3, dh3;
    public String im1, im2;
    private static Image img1, img2;
    
    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        dx1 = (int) (x1 * xk); dy1 = (int) (y1 * yk);
        dw1 = (int) (w1 * xk); dh1 = (int) (h1 * yk);
        dx2 = (int) (x2 * xk); dy2 = (int) (y2 * yk);
        dw2 = (int) (w2 * xk); dh2 = (int) (h2 * yk);
        dy3 = (int) (y3 * xk); dh3 = (int) (h3 * yk);
        if (im1!=null) {
            img1 = art.soft.utils.Image.loadImage(im1);
            im1 = null;
        }
        if (im2!=null) {
            img2 = art.soft.utils.Image.loadImage(im2);
            im2 = null;
        }
    }

    @Override
    public void draw(art.soft.Graphics g){
        if (unit!=null) {
            g.draw(img1, x, y, width, height);
            g.draw(unit.uData.Icon, x+dx1, y+dy1, dw1, dh1);
            g.setColor(Color.GREEN);
            g.fillRect(x + dx2, y + dy2, (int) (dw2 * unit.getHPper()), dh2);
            g.setColor(Color.BLUE);
            g.fillRect(x + dx2, y + dy3, (int) (dw2 * unit.getMPper()), dh3);
            if (numUnit==Game.yourPlayer.gUnit.selNum)
                g.draw(img2, x, y, width, height);
        }
    }

    @Override
    public void pressed(int code, int k){
        if (code==Interface.BUTTON_PRESSED) {
            if (unit!=null) {
                if (Game.statusKey==Game.DO_ATTACK) {
                    Game.CShost.setAttackUnit(unit);
                } else if (Game.statusKey==Game.DO_SELECT_SPELL) {
                    if ((Game.castSpell.attrib & Spell.CAST_ON_UNIT)!=0 &&
                    (unit!=Game.yourPlayer.gUnit.selUnit || (Game.castSpell.influents & Spell.YOUR_AURA)!=0))
                        Game.CShost.castSpellOnUnit(Game.spellNum, unit);
                    else if ((Game.castSpell.attrib & Spell.CAST_ON_AOE)!=0)
                        Game.CShost.castSpellToPos(Game.spellNum, unit.px, unit.py);
                } else if (Game.statusKey==Game.DO_LEVEL_UP) {
                    // Game.CShost.playerLearnStats();
                } else if (numUnit==Game.yourPlayer.gUnit.selNum) {
                    Game.CShost.selectUnit(unit);
                    Game.curCam.moveCam(unit.px, unit.py);
                } else {
                    Game.CShost.selectUnitFormGroup(numUnit);
                }
            }
        }
        if (code!=Interface.MOUSE_OVER) Game.statusKey = Game.DO_NOTHING;
    }

    @Override
    public void dispose(){
        super.dispose();
        if (img1!=null) img1.dispose();
        if (img2!=null) img2.dispose();
    }

    @Override
    public void draggedOver(DraggedBut target) {
        if (target instanceof ItemBut) {
            curCur = CURSOR_SPELL_WALK_TO;
        }
    }

    @Override
    public void DropIn(DraggedBut target) {
        if (target instanceof ItemBut) {
            CShost.dropItemToUnit(((ItemBut) target).num, unit);
        }
    }
}
