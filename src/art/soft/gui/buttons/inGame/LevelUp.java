package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.gui.Interface;
import art.soft.gui.buttons.TButton;
import static art.soft.gui.Interface.MOUSE_OVER;
import art.soft.units.Unit;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class LevelUp extends TButton {

    private float loop, cyc;
    public String border;
    public Image im2;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        if (border!=null) {
            im2 = art.soft.utils.Image.loadImage(border);
            border = null;
        }
    }

    @Override
    public void draw(Graphics g){
        active = false;
        Unit u = Game.yourPlayer.gUnit.selUnit;
        if (u!=null && Game.yourPlayer.owner.groupInGroup(u.canControl) && u.uLvl!=null) {
            int l = u.uLvl.level - u.uLvl.old_level;
            if (l>0 && (u.spells!=null || u.attribSpell!=null)) {
                active = true;
                String s = text;
                setText(text + l);
                if (loop<0.5) { loop = 0.5f; cyc = 1000f; }
                loop += Game.tFrame / cyc;
                if (loop>1f) { loop = 1f; cyc = -1000f; }
                int wh = (int) ((width >> 1) * loop);
                int h = (int) (height * loop);
                g.draw(im2, x - (wh>>1), y - h, width + wh, height + h + h);
                super.draw(g);
                text = s;
            } else
                if (Game.statusKey==Game.DO_LEVEL_UP) Game.statusKey = Game.DO_NOTHING;
        }
    }

    @Override
    public void pressed(int code, int k){
        if (code!=MOUSE_OVER) Game.statusKey = Game.DO_NOTHING;
        if (code==Interface.BUTTON_PRESSED || code==Interface.BUTTON_RELESSED) {
            Game.statusKey = Game.DO_LEVEL_UP;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (im2!=null) im2.dispose();
    }
}
