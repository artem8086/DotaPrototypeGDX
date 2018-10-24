package art.soft.gui.buttons.menu;

import art.soft.Graphics;
import art.soft.gui.buttons.HScroll;
import art.soft.gui.buttons.IButton;
import art.soft.gui.buttons.TButton;
import art.soft.gui.buttons.TextBox;
import art.soft.map.mapConfig;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class mapInfo extends HScroll {

    public static mapInfo instance;
    public int[] author, name, version, icon, describe;

    public IButton getButton(int[] but) {
        if (but!=null) {
            IButton b = this;
            for (int i=0; i<but.length; i ++) {
                b = b.buttons[but[i]];
            }
            return b;
        } else return null;
    }

    @Override
    public void langReload() {
        instance = this;
        super.langReload();
        mapConfig m = mapsPool.customMap;
        if (m!=null) {
            ((TButton) getButton(author)).text = m.getAuthor();
            ((TButton) getButton(name)).text = m.getName();
            ((TButton) getButton(version)).text = m.getVersion();
            TextBox tb = ((TextBox) getButton(describe));
            tb.keyWord = m.getText();
            tb.langReload();
            Image i = m.mapImage;
            getButton(icon).im = i==null ? im : i;
        } else {
            ((TButton) getButton(author)).text = null;
            ((TButton) getButton(name)).text = null;
            ((TButton) getButton(version)).text = null;
            ((TextBox) getButton(describe)).text = null;
            getButton(icon).im = im;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (mapsPool.customMap!=null) {
            active = true;
            super.draw(g);
        } else active = false;
    }

    @Override
    public void dispose() {
        if (mapsPool.customMap!=null) {
            mapsPool.customMap.dispose();
            mapsPool.customMap = null;
        }
    }
}
