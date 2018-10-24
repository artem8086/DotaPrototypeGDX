package art.soft.gui.buttons;

import art.soft.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

/**
 *
 * @author Артем
 */
public class TButton extends IButton {

    public String text, keyWord;
    public int fontSize, color, mode = Align.center;
    public boolean bold;
    public BitmapFont font;
    
    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        keyWord = text;
        langReload();
    }

    @Override
    public void langReload(){
        super.langReload();
        font = Game.getFont(fontSize, bold);
        setText(Game.language.getText(keyWord));
    }

    public void setText(String text){
        this.text = text;
    }

    @Override
    public void draw(art.soft.Graphics g){
        super.draw(g);
        if (text!=null) {
            g.setFont(font);
            g.setColor(color);
            int h = (g.fontAscent() - (g.fontHeight()>>1));
            g.drawString(text, x, y + h + (height>>1), width, mode);
        }
    }
}
