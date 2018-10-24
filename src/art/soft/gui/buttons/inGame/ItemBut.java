package art.soft.gui.buttons.inGame;

import art.soft.Game;
import static art.soft.Game.CShost;
import static art.soft.Game.CURSOR_SPELL_DEFAULT;
import static art.soft.Game.curCur;
import art.soft.Graphics;
import art.soft.gui.Interface;
import art.soft.gui.buttons.DragTarget;
import art.soft.gui.buttons.DraggedBut;
import art.soft.gui.buttons.TButton;
import static art.soft.gui.buttons.inGame.Ability.drawCD;
import art.soft.gui.buttons.shop.ShopBut;
import art.soft.gui.buttons.shop.ShopButList;
import art.soft.items.Item;
import static art.soft.items.ItemData.ITEM_SHOW_NUMBER;
import art.soft.items.ShopItem;
import art.soft.spells.Effect;
import art.soft.spells.Spell;
import art.soft.units.Unit;
import static art.soft.units.Unit.CANT_USE_ITEM;
import art.soft.utils.Image;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 *
 * @author Artem
 */
public class ItemBut extends TButton implements DragTarget, DraggedBut {

    public static final int GRAY_COLOR = 0x44FFFFFF;

    public static int itemButW, itemButH;
    
    public int num, fontSize2;
    private Spell spell;
    public Unit item;
    private Unit u;
    public String emptyItem, defaultItem;
    public static Image eItem, dItem;

    public static BitmapFont font2;
    public int keyD;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        itemButW = width;
        itemButH = height;
        keyD = key;
        if (fontSize2!=0)
            font2 = Game.getFont(fontSize2, bold);
        if (emptyItem!=null) {
            eItem = art.soft.utils.Image.loadImage(emptyItem);
            emptyItem = null;
        }
        if (defaultItem!=null) {
            dItem = art.soft.utils.Image.loadImage(defaultItem);
            defaultItem = null;
        }
    }

    @Override
    public void draw(art.soft.Graphics g){
        key = 0;
        active = false;
        im = eItem;
        u = Game.yourPlayer.gUnit.selUnit;
        if (u!=null && u.items!=null && u.items.length>num) {
            boolean vis = Game.yourPlayer.VisiblePlayers.groupInGroup(u.visGRP);
            text = null;
            if (vis) item = u.items[num];
            if (item!=null) {
                Item it = item.item;
                if (it!=null) {
                    spell = it.spell;
                    if (Game.yourPlayer.owner.groupInGroup(u.canControl) &&
                            item.owners.youInGroup()) active = true;
                    else g.setAlpha(GRAY_COLOR);
                    Image i = spell!=null ? spell.getIcon() : null;
                    if (i==null) i = item.uData.Icon;
                    if (i==null) i = dItem;
                    im = i;
                    if (active && spell!=null) {
                        Effect e = spell.effect;
                        boolean haveMana = e!=null && vis ? e.haveMana() : true;
                        int cd = vis ? spell.getCooldown() : 0;
                        if ((spell.attrib & Spell.IS_ACTIVE_SPELL)!=0)
                            if (haveMana && cd<=0) key = keyD;
                        g.draw(im, x, y, width, height);
                        g.setAlphaNormal();
                        if (cd>0) {
                            drawCD(g, this, cd, spell.getCooldownTime());
                            int c = cd / 1000;
                            if (c!=0) setText("" + c);
                            else setText("0." + (cd / 100));
                            active = false;
                        }
                        im = null;
                        super.draw(g);
                        im = i;
                    } else {
                        super.draw(g);
                    }
                    g.setAlphaNormal();
                    if (spell!=null && (it.attrib & ITEM_SHOW_NUMBER)!=0) {
                        g.setFont(font2);
                        g.setColor(color);
                        String s = "" + spell.number;
                        int w = g.stringWidth(s);
                        g.drawString(s, x - w - 2 + width, y - g.fontCapHeight() - 2 + height);
                    }
                } else {
                    if (Game.yourPlayer.owner.groupInGroup(u.canControl)) {
                        key = keyD;
                        active = true;
                    }
                    im = item.uData.Icon;
                    if (im==null) im = dItem;
                    super.draw(g);
                }
            } else {
                g.draw(eItem, x, y, width, height);
            }
        } else {
            g.draw(eItem, x, y, width, height);
        }
        if (Interface.dragged) active = true;
    }

    @Override
    protected void keyPressed(int key) {
        pressed(Interface.BUTTON_RELESSED, key);
    }

    @Override
    protected void keyRelessed(int key) {}

    @Override
    public void pressed(int code, int k){
        if (code==Interface.MOUSE_OVER) {
            if (spell!=null && spell.radius>0) {
                Game.circleR = spell.radius;
                Game.circleX = u.px;
                Game.circleY = u.py;
            }
        } else
        if (code==Interface.BUTTON_RELESSED) {
            if (key!=0 && (u.status & CANT_USE_ITEM)==0) {
                if (spell==null) {
                    CShost.useItemAlready(num);
                    Game.statusKey = Game.DO_NOTHING;
                } else {
                    if ((spell.attrib & Spell.CAST_ALREADY)!=0) {
                        CShost.useItemAlready(num);
                    } else {
                        Game.statusKey = Game.DO_SELECT_ITEM;
                        Game.castSpell = spell;
                        Game.spellNum = num;
                    }
                }
            } else {
                Game.statusKey = Game.DO_NOTHING;
            }
        } else
            Game.statusKey = Game.DO_NOTHING;
    }

    @Override
    public void dispose(){
        super.dispose();
        if (eItem!=null) { eItem.dispose(); eItem = null; }
        if (dItem!=null) { dItem.dispose(); dItem = null; }
    }

    @Override
    public void draggedOver(DraggedBut target) {
        if (target instanceof ItemBut || target instanceof ShopBut || target instanceof ShopButList) {
            curCur = CURSOR_SPELL_DEFAULT;
        }
    }

    @Override
    public void DropIn(DraggedBut target) {
        if (target instanceof ItemBut) {
            CShost.playerChangeItemSlot(num, ((ItemBut) target).num);
        } else
        if (target instanceof ShopBut) {
            ShopItem item = ((ShopBut) target).item;
            if (item!=null) CShost.playerBuyItem(item.shopID, item.ID);
        } else
        if (target instanceof ShopButList) {
            ShopItem item = ((ShopButList) target).item;
            if (item!=null) CShost.playerBuyItem(item.shopID, item.ID);
        }
    }

    @Override
    public void DraggedDraw(Graphics g, int x, int y) {
        if (im!=null) {
            g.draw(im, x - (width>>1), y - (height>>1), width, height);
	}
    }
}
