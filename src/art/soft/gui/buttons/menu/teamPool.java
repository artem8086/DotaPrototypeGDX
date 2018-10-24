package art.soft.gui.buttons.menu;

import art.soft.Graphics;
import art.soft.gui.buttons.IButton;
import art.soft.gui.buttons.TButton;
import art.soft.multiplayer.Player;
import art.soft.multiplayer.PlayerTeam;
import com.badlogic.gdx.graphics.Color;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class teamPool extends TButton {

    public int imgW, butH, col;
    public Image icon;

    public static teamPool createBut(int x, int y, int w, int distX, int distY,
            int imgW, int butH, int color, int fontSize, boolean bold, PlayerTeam pt){
        teamPool tp = new teamPool();
        tp.x = x; tp.y = y;
        tp.width = w;
        tp.imgW = imgW;
        tp.butH = butH;
        tp.fontSize = fontSize;
        tp.bold = bold;
        tp.color = color;
        tp.col = pt.color;
        Player p = pt.getPlayers();
        tp.height = butH + distY;
        IButton b[] = tp.buttons = new IButton[pt.num];
        int i = 0;
        while (p!=null) {
            b[i ++] = PlayerBut.createBut(x+distX, y+tp.height, w-distX-distX,
                    butH, color, fontSize, bold, p);
            tp.height += butH + distY;
            p = p.next;
        }
        tp.icon = pt.ic;
        tp.keyWord = pt.nameK;
        tp.langReload();
        return tp;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(col);
        g.fillRect(x, y, width, butH);
        g.setStroke(2);
        g.drawRect(x, y, width, height);
        if (icon!=null) g.draw(icon, x+2, y+2, imgW, butH-4);
        int h = height;
        height = butH;
        super.draw(g);
        height = h;
    }
}
