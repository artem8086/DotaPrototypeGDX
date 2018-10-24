package art.soft.gui.buttons.menu;

import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.multiplayer.ClientServer;
import art.soft.multiplayer.Player;

/**
 *
 * @author Artem
 */
public class lockDiscon extends IButton {
    
    public Player player;
    
    public static lockDiscon createBut(int x, int y, int w, int h, Player pl){
        lockDiscon ld = new lockDiscon();
        ld.x = x; ld.y = y;
        ld.width = w; ld.height = h;
        ld.player = pl;
        return ld;
    }

    @Override
    public void draw(art.soft.Graphics g){
        if (PlayerBut.isServer) {
            im = null;
            if (player!=ClientServer.yourPlayer) {
                if (player.inUse) im = PlayersField.kickOff;
                else if (player.lock) im = PlayersField.unlock;
                else im = PlayersField.lock;
            }
            active = im!=null;
            super.draw(g);
        } else
            active = false;
    }

    @Override
    public void pressed(int code, int k){
        if (code==Interface.BUTTON_PRESSED) {
            if (im==PlayersField.unlock) player.lock = false;
            else if (im==PlayersField.lock) player.lock = true;
            else player.kick_off();
        }
    }
}
