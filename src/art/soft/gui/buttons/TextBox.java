package art.soft.gui.buttons;

import art.soft.Game;
import art.soft.Graphics;
import com.badlogic.gdx.utils.Align;

/**
 *
 * @author Artem
 */
public class TextBox extends TButton {

    public int mode = Align.topLeft;
    public boolean fromFile = false;

    @Override
    public void langReload(){
        super.langReload();
        if (fromFile && text!=null) {
            text = Game.openFile(text).readString("UTF-8");
        }
    }

    @Override
    public void draw(Graphics g){
        String t = text;
        text = null;
        super.draw(g);
        text = t;
        if (t!=null) {
            g.setFont(font);
            g.setColor(color);
            height = (int) g.drawMultiLine(t, x, y, width, mode).height;
        }
    }
}