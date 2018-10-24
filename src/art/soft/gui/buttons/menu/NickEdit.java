package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.buttons.EditBox;

/**
 *
 * @author Artem
 */
public class NickEdit extends EditBox {
    
    @Override
    public void load(double xk, double yk){
        if (Game.profile.name!=null) text = Game.profile.name;
        super.load(xk, yk);
    }

    @Override
    public void draw(art.soft.Graphics g){
        super.draw(g);
        Game.profile.name = text;
    }
}
