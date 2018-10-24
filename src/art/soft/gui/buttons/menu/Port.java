package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.buttons.EditBox;

/**
 *
 * @author Artem
 */
public class Port extends EditBox {
    
    @Override
    public void draw(art.soft.Graphics g){
        super.draw(g);
        Game.portT = text;
    }
}
