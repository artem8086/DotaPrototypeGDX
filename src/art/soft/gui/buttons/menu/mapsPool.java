package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.gui.buttons.WScroll;
import art.soft.map.GameMap;
import art.soft.map.mapConfig;
import art.soft.map.mapsLink;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class mapsPool extends WScroll {

    public static mapConfig customMap;
    public mapName sel;
    public boolean draw = true;
    public int imW, xt, wt, movX;

    @Override
    public void load(double xk, double yk){
        mapsLink m = Game.json.fromJson(mapsLink.class, Game.openFile(GameMap.MAPS_PATH+text));
        text = null;
        super.load(xk, yk);
        imW *= xk;
        int i = m.maps.length;
        buttons = new IButton[i];
        wt = (width>>1) - ((imW+xt)>>1);
        for (i --; i>=0; i --) {
            mapsLink t = m.maps[i];
            t.Load();
            if (t.smal==null) t.smal = im;
            buttons[i] = mapName.createBut(i==0 ? wt : xt, y, imW, height, font, color, t);
        }
        movX = textX;
    }

    @Override
    public void draw(Graphics g) {
        if (draw) {
            Image i = im;
            im = null;
            super.draw(g);
            super.onGUI(width >> 1, height >> 1);
            if (but!=null) but.pressed(Interface.MOUSE_OVER, 0);
            else but = null;
            if (movX==textX) {
                if (sel!=mapName.sel) {
                    if (customMap!=null) {
                        customMap.dispose();
                        customMap = null;
                    }
                    String path = mapName.sel.map.mapConfig;
                    if (path!=null) {
                        customMap = Game.json.fromJson(mapConfig.class,
                                Game.openFile(GameMap.MAPS_PATH+path));
                        customMap.Load();
                        mapInfo.instance.langReload();
                    }
                }
                sel = mapName.sel;
            }
            movX = textX;
            im = i;
        }
    }

    @Override
    public IButton onGUI(int x, int y){
        IButton s[] = buttons;
        buttons = null;
        IButton b = super.onGUI(x, y);
        buttons = s;
        return b;
    }

    @Override
    public void drawElements(Graphics g){
        super.drawElements(g);
        poolW += wt;
    }
}
