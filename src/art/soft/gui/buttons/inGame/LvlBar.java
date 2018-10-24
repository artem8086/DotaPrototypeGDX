package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.buttons.TButton;
import art.soft.units.ULevelHero;
import art.soft.units.Unit;
import art.soft.utils.Image;

/**
 *
 * @author Артем
 */
public class LvlBar extends TButton {
    
    private int sk, sp;
    
    @Override
    public void draw(art.soft.Graphics g){
        Image i = im;
        Unit u = Game.yourPlayer.gUnit.selUnit;
        text = null;
        if (u!=null) {
            if (u.uLvl instanceof ULevelHero && Game.yourPlayer.FrendPlayer.groupInGroup(u.owners)) {
                if (Game.yourPlayer.owner.groupInGroup(u.isVisible)) {
                    sk = u.uLvl.getSkill();
                    sp = u.uLvl.getSkillPool();
                }
                setText("" + sk + "/" + sp);
                if (i!=null) {
                    float k = (float) sk / sp;
                    g.draw(i, x, y, (int) (width * k), height,
                            0, 0, (int) (i.getWidth() * k), i.getHeight());
                }
            }
        }
        im = null;
        super.draw(g);
        im = i;
    }
}
