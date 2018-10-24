package art.soft.gui.buttons.menu;

import art.soft.gui.Language;
import art.soft.gui.buttons.IButton;
import art.soft.gui.buttons.TButton;
import art.soft.multiplayer.Player;

/**
 *
 * @author Artem
 */
public class PlayerBut extends TButton {

    public static boolean isServer;

    public Player player;

    public static PlayerBut createBut(int x, int y, int w, int h, int color, int fontSize, boolean bold, Player pl){
        PlayerBut cp = new PlayerBut();
        cp.x = x; cp.y = y;
        cp.width = w; cp.height = h;
        cp.fontSize = fontSize;
        cp.bold = bold;
        cp.color = color;
        cp.player = pl;
        cp.im = PlayersField.Background;
        cp.buttons = new IButton[1];
        cp.buttons[0] = lockDiscon.createBut(x+w-h+4, y+4, h-8, h-8, pl);
        cp.langReload();
        return cp;
    }

    @Override
    public void draw(art.soft.Graphics g){
        String old = text;
        if (player.inUse) {
            setText(player.name);
        } else if (player.lock) text = Language.words.locked;
        else text = Language.words.openSlot;
        if (old!=text) setText(text);
        super.draw(g);
    }
}
