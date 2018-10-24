package art.soft.gui.buttons.shop;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.gui.Interface;
import art.soft.gui.buttons.DragTarget;
import art.soft.gui.buttons.DraggedBut;
import art.soft.gui.buttons.IButton;
import art.soft.gui.buttons.inGame.ItemBut;
import art.soft.items.ItemData;
import art.soft.items.ShopItem;
import art.soft.units.Unit;
import art.soft.units.UnitData;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Artem
 */
public class ItemDetail extends IButton implements DragTarget {

    static ItemDetail instance;
    private static int x1, y1;
    private static ShopBut res = new ShopBut();

    private int colOver;
    public int color, deltaW;
    public float deltaT;
    private float scaleW;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        instance = this;
        colOver = color;
        deltaW *= xk;
        color = -1;
    }
    
    @Override
    public void draw(Graphics g) {
        ShopBut but = (ShopBut) buttons[1];
        if (Interface.dragged) {
            IButton i = (IButton) Interface.draggedBut;
            if (i instanceof ShopBut || i instanceof ItemBut) {
                if (i!=but && i!=res) color = colOver;
            } else color = -1;
        } else color = -1;
        if (color!=-1) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
        g.setStroke(2);
        x1 = but.x + (but.width >> 1);
        y1 = but.y + (but.height >> 1);
        ShopItem item = but.item;
        if (item!=null) {
            ItemData it = item.uData.itemData;
            if (it!=null) {
                UnitData[] items = it.nextItems;
                if (items!=null)
                    drawButtons(g, items, (ShopBut) buttons[0], items.length - it.showNextItemsNum);
                items = it.receptItems;
                if (items!=null)
                    drawButtons(g, items, (ShopBut) buttons[2], items.length - it.showReceptItemsNum);
            }
            but.draw(g);
            scaleW += deltaT * Game.deltaT;
            if (scaleW>1) scaleW = 1;
        }
    }

    public void drawButtons(Graphics g, UnitData items[], ShopBut but, int n) {
        int delta = (int) ((but.width + deltaW) * scaleW);
        int tx = - (delta * n - delta) >> 1;
        for (int i=0; i<n; i ++) {
            but.x += tx;
            int x2 = but.x + (but.width >> 1);
            int y2 = but.y + (but.height >> 1);
            g.setColor(Color.WHITE);
            g.drawLine(x1, y1, x2, y2);
            but.item = items[i].shopData;
            but.draw(g);
            but.x -= tx;
            tx += delta;
        }
    }

    public boolean copyButtons(UnitData items[], ShopBut but, int n, ShopBut res) {
        int delta = (int) ((but.width + deltaW) * scaleW);
        int tx = - (delta * n - delta) >> 1;
        boolean t = false;
        for (int i=0; i<n; i ++) {
            but.x += tx;
            int x2 = but.x + (but.width >> 1);
            int y2 = but.y + (but.height >> 1);
            but.item = items[i].shopData;
            if (but.contains(x1, y1)) {
                but.setToBut(res);
                t = true;
            }
            but.x -= tx;
            tx += delta;
        }
        return t;
    }

    @Override
    public IButton onGUI(int x, int y){
        if (active) {
            if (contains(x, y)) {
                if (Interface.dragged==false) {
                    ShopBut but = (ShopBut) buttons[1];
                    x1 = x; y1 = y;
                    ShopItem item = but.item;
                    if (item!=null) {
                        ItemData it = item.uData.itemData;
                        if (it!=null) {
                            UnitData[] items = it.nextItems;
                            if (items!=null)
                                if (copyButtons(items, (ShopBut) buttons[0], items.length - it.showNextItemsNum, res))
                                    return res;
                            items = it.receptItems;
                            if (items!=null)
                                if (copyButtons(items, (ShopBut) buttons[2], items.length - it.showReceptItemsNum, res))
                                    return res;
                        }
                    }
                    if (but.contains(x, y)) return but;
                }
                return this;
            }
        }
        return null;
    }

    public void setItem(ShopItem it) {
        ((ShopBut)  buttons[1]).item = it;
        scaleW = 0;
    }

    @Override
    public void draggedOver(DraggedBut target) {
        if (target instanceof ItemBut) {
            color = colOver;
        } else color = -1;
    }

    @Override
    public void DropIn(DraggedBut target) {
        if (target instanceof ShopBut) {
            setItem(((ShopBut) target).item);
        } else
        if (target instanceof ShopButList) {
            setItem(((ShopButList) target).item);
        } else
        if (target instanceof ItemBut) {
            Unit i = ((ItemBut) target).item;
            if (i!=null) setItem(i.uData.shopData);
        }
    }
}