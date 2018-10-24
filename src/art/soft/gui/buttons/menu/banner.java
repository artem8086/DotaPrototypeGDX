package art.soft.gui.buttons.menu;

import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Artem
 */
public class banner extends IButton{
    
    public String url;
    public boolean mover = false;

    @Override
    public void draw(art.soft.Graphics g){
        super.draw(g);
        if (mover) {
            g.setColor(Color.RED);
            g.setStroke(2);
            g.drawRect(x, y, width, height);
        }
        mover = false;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
        if (code==Interface.BUTTON_PRESSED) {
            Gdx.net.openURI(url);
        }
    }
}
