package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.buttons.TButton;
import com.badlogic.gdx.graphics.Color;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class PlayBut extends TButton {

    private final static float start = 0.15f, end = 0.8f, step = 2f;
    private static Image softbox;
    public String back, backPrsd, soft;
    public float alpha;
    public Image im1, im2;
    public int win[], boxCol = 0xFFFFFF;
    public boolean mover = false;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        if (soft!=null) {
            softbox = art.soft.utils.Image.loadImage(soft);
            soft = null;
        }
        if (back!=null) {
            im1 = art.soft.utils.Image.loadImage(back);
            back = null;
        }
        if (backPrsd!=null) {
            im2 = art.soft.utils.Image.loadImage(backPrsd);
            backPrsd = null;
        }
        alpha = start;
    }

    @Override
    public void draw(art.soft.Graphics g){
        g.setColor(Color.GRAY);
        if (boxCol!=-1) {
            if (mover) {
                alpha += step * Game.deltaT;
                if (alpha>end) alpha = end;
            } else {
                alpha -= step * Game.deltaT;
                if (alpha<start) alpha = start;
            }
            int wh = (int) ((width >> 1) * 0.12f);
            int h = (int) (height * 0.18f);
            g.setAlpha(boxCol);
            g.setAlpha(alpha);
            g.draw(softbox, x - (wh>>1), y - h, width + wh, height + h + h);
            g.setAlphaNormal();
        }
        Image i = mover ? im2 : im1;
        if (i!=null) g.draw(i, x, y, width, height);
        super.draw(g);
        mover = false;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            Interface w = Interface.getWindow(win);
            if (w!=null) w.openWin();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (im1!=null) im1.dispose();
        if (im2!=null) im2.dispose();
        if (softbox!=null) {
            softbox.dispose();
            softbox = null;
        }
    }
}
