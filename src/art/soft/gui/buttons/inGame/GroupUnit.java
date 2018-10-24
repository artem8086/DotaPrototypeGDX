package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.buttons.IButton;
import art.soft.units.UnitGroup;
import art.soft.units.UnitGroupSelect;

/**
 *
 * @author Артем
 */
public class GroupUnit extends IButton {

    public IButton[] pages;
    public static int page;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        for (IButton p : pages)
            p.load(xk, yk);
    }

    @Override
    public void draw(art.soft.Graphics g){
        if (active = (Game.yourPlayer.gUnit.next!=null)) {
            int len = buttons.length;
            int plen = pages.length;
            UnitGroupSelect ugs = Game.yourPlayer.gUnit;
            UnitGroup u = ugs;
            int i = 0;
            int n = 0;
            int p = 0;
            while (u!=null) {
                UnitIcon ui = ((UnitIcon) buttons[i]);
                ui.unit = u.unit;
                ui.numUnit = n;
                i ++; n ++;
                if (i>=len) {
                    pages[p].active = true;
                    if (p==page || p>=plen) break;
                    p ++; i = 0;
                }
                u = (UnitGroup) u.next;
            }
            for (int j = i; j<len; j ++) {
                ((UnitIcon) buttons[j]).unit = null;
            }
            while (u!=null) {
                if (i==0) {
                    i = 0;
                    if (p>=plen) break;
                    p ++;
                    if (p<plen) pages[p].active = true;
                }
                i ++;
                if (i>=len) i = 0;
                u = (UnitGroup) u.next;
            }
            if (p==0) p = -1;
            for (p ++; p<plen; p ++) pages[p].active = false;

            super.draw(g);
            for (IButton b : pages) b.draw(g);
        }
    }

    @Override
    public IButton onGUI(int x, int y){
        IButton but = super.onGUI(x, y);
        if (active) {
            IButton b;
            for (IButton p : pages) {
                b = p.onGUI(x, y);
                if (b!=null) return b;
            }
        }
        return but;
    }
}
