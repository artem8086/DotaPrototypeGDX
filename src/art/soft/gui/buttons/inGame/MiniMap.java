package art.soft.gui.buttons.inGame;

import art.soft.Game;
import static art.soft.Game.curCur;
import static art.soft.Game.CShost;
import static art.soft.Game.CURSOR_SPELL_DEFAULT;
import art.soft.Graphics;
import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import static art.soft.gui.Interface.MOUSE_OVER;
import art.soft.gui.buttons.DragTarget;
import art.soft.gui.buttons.DraggedBut;
import art.soft.map.GameMap;
import static art.soft.map.mapEffect.EFFECT_ATTACK;
import static art.soft.map.mapEffect.EFFECT_ATTACK_WAY;
import static art.soft.map.mapEffect.EFFECT_MOVE_TO;
import static art.soft.map.mapEffect.EFFECT_MOVE_WAY;
import art.soft.specal.poolEffects;
import art.soft.spells.Spell;
import art.soft.triggers.refreshEffects;
import art.soft.units.Animation;
import art.soft.units.Unit;
import com.badlogic.gdx.graphics.Color;
import art.soft.utils.Rectangle;
import com.badlogic.gdx.math.MathUtils;

/**
 *
 * @author Артем
 */
public class MiniMap extends IButton implements DragTarget {

    public static final int COLOR_GREEN = 0xFF00;
    public static final int COLOR_RED = 0xFF0000;

    public static final int CREEP_TEAM = 0;
    public static final int CREEP_ENEMY = 1;
    public static final int TEAM_HERO = 2;
    public static final int ENEMY_HERO = 3;
    public static final int YOUR_HERO = 4;

    public int mi_w, mi_h;
    public double kx, ky, tx, ty;
    public poolEffects effects = new poolEffects();
    public Animation miniIcons;
    
    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        mi_w *= xk;
        mi_h *= yk;
        refreshEffects.miniMap = this;
        if (Game.map!=null) Init();
    }

    public void Init(){
        double s = Game.map.getSizePix();
        miniIcons = Game.map.getAnimation(2, 1);
        kx = s / width;
        ky = s / height;
        tx = width  / s;
        ty = height / s;
    }

    public void postDraw(Graphics g){
        Rectangle rt = Game.curCam.rect;
        Game.curCam.rect = this;
        effects.refreshEffects(g);
        Game.curCam.rect = rt;
        g.setColor(Color.GREEN);
        g.setStroke(1);
        Rectangle r = Game.curCam.rect;
        int w = (int) (r.width * tx);
        if (w>width) w = width;
        int h = (int) (r.height * ty);
        if (h > height) h = height;
        g.drawRect((int) (r.x * tx) + x, (int) (r.y * ty) + y, w, h);
    }
    
    @Override
    public void draw(Graphics g) {}

    public void preDraw(Graphics g) {
        im = GameMap.miniMapImage;
        super.draw(g);
    }

    public int getMiniMapX(int xc) {
        return (int) (tx * xc) + x;
    }

    public int getMiniMapY(int yc) {
        return (int) (ty * yc) + y;
    }

    @Override
    public void pressed(int code, int k){
        int x = (int) (kx * curWin.mbx);
        int y = (int) (ky * curWin.mby);
        if( code==Interface.BUTTON_PRESSED ){
            if (Game.statusKey==Game.DO_ATTACK) {
                Game.CShost.setAttackPos( x, y );
                Game.map.addEffect(x, y, Game.shift ? EFFECT_ATTACK_WAY : EFFECT_ATTACK, 1f, true, -1);
            } else if (Game.statusKey==Game.DO_SELECT_SPELL) {
                if ((Game.castSpell.attrib & Spell.CAST_ON_AOE)!=0)
                    Game.CShost.castSpellToPos(Game.spellNum, x, y);
            } else if (Game.statusKey==Game.DO_SELECT_ITEM) {
                if ((Game.castSpell.attrib & Spell.CAST_ON_AOE)!=0)
                    Game.CShost.useItemlToPos(Game.spellNum, x, y);
            } else if (Game.control) {
                CShost.playerPing(x, y, true);
                Game.statusKey = Game.DO_NOTHING;
            } else if (Game.alt) {
                CShost.playerPing(x, y, false);
                Game.statusKey = Game.DO_NOTHING;
            } else {
                Interface.butClear = false;
                Game.curCam.moveCam(x, y);
            }
            Game.statusKey = Game.DO_NOTHING;
        }
        if (code!=MOUSE_OVER) Game.statusKey = Game.DO_NOTHING;
        if( code==Interface.ALTERNATIVE_PRESSED ){
            Game.CShost.setMovePos(x, y);
            Game.map.addEffect(x, y, Game.shift ? EFFECT_MOVE_WAY : EFFECT_MOVE_TO, 1f, true, -1);
        }
    }
    
    public void drawUnit(Graphics g, Unit u){
        int x = (int) (tx * u.px) + this.x;
        int y = (int) (ty * u.py) + this.y;

        if (Game.alt && u.uData.mIcon!=null) {
            g.draw(u.uData.mIcon, x - (mi_w>>1), y-(mi_h>>1), mi_w, mi_h);
        } else {
            boolean team = Game.yourPlayer.FrendPlayer.groupInGroup(u.owners);

            if (u.uData.isHero) {
                g.setAlpha(team ? COLOR_GREEN : COLOR_RED);
                if (u.owners.youInGroup()) miniIcons.setFastAnim_(YOUR_HERO);
                else miniIcons.setFastAnim_(team ? TEAM_HERO : ENEMY_HERO);
            } else {
                miniIcons.setFastAnim_(team ? CREEP_TEAM : CREEP_ENEMY);
            }
            miniIcons.play(g, x, y, u.angle * MathUtils.radiansToDegrees);
            g.setAlphaNormal();
        }
    }

    @Override
    public void dispose() {
        im = null;
        super.dispose();
    }

    @Override
    public void draggedOver(DraggedBut target) {
        if (target instanceof ItemBut) {
            curCur = CURSOR_SPELL_DEFAULT;
        }
    }

    @Override
    public void DropIn(DraggedBut target) {
        if (target instanceof ItemBut) {
            CShost.dropItemToPos(((ItemBut) target).num, (int) (kx * curWin.mbx), (int) (ky * curWin.mby));
        }
    }
}
