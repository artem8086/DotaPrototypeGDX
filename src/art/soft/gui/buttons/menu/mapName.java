package art.soft.gui.buttons.menu;

import art.soft.Graphics;
import art.soft.gui.buttons.TButton;
import art.soft.map.mapsLink;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

/**
 *
 * @author Artem
 */
public class mapName extends TButton {

    public static mapName sel;
    public mapsLink map;

    public static mapName createBut(int x, int y, int w, int h, BitmapFont font, int c, mapsLink m){
        mapName lb = new mapName();
        lb.x = x; lb.y = y;
        lb.width = w; lb.height = h;
        lb.color = c;
        lb.font = font;
        lb.text = m.mapName;
        lb.mode = Align.left;
        lb.map = m;
        return lb;
    }

    @Override
    public void langReload(){}

    @Override
    public void draw(Graphics g) {
        g.draw(map.smal, x, y, width, height);
        g.setColor(color);
        if (text!=null) {
            g.setFont(font);
            g.drawString(text, x + 2, y + 2);
        }
        if (sel==this) g.drawRect(x+1, y+1, width-2, height-2);
    }

        @Override
    public void pressed(int code, int k){
        sel = this;
    }
}
