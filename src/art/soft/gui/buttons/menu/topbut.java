package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.buttons.TButton;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class topbut extends TButton {

    public String topimg;
    public Image tim, sel;
    public int color1, color2, win, winPath[];

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        color1 = color;
        sel = im;
        im = null;
        if (topimg!=null) {
            tim = art.soft.utils.Image.loadImage(topimg);
            topimg = null;
        }
        if (win<curWin.activeWin) {
            curWin.activeWin = win;
            if (tim!=null)
            topbar.instance.topbar = topbar.instance.imt = tim;
        }
    }

    @Override
    public void draw(art.soft.Graphics g){
        if (curWin.activeWin==win) { color = color2; }
        super.draw(g);
        if (buttons!=null) buttons[0].im = null;
        color = color1;
    }

    @Override
    public void pressed(int code, int k){
        if (curWin.activeWin!=win) {
            if (buttons!=null) buttons[0].im = sel;
            color = color2;
            if (code==Interface.BUTTON_PRESSED) {
                topbar.instance.setImage(tim);
                winPath[winPath.length-1] = curWin.activeWin;
                Interface w = Interface.getWindow(winPath);
                if (w!=null) w.hideWin();
                winPath[winPath.length-1] = curWin.activeWin = win;
                w = Interface.getWindow(winPath);
                if (w!=null) w.openWin();
            }
        }
    }

    @Override
    public void dispose(){
        if (tim!=null) tim.dispose();
        if (sel!=null) sel.dispose();
    }
}
