package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.buttons.HScroll;
import art.soft.gui.buttons.IButton;
import static art.soft.gui.buttons.IButton.curWin;
import static art.soft.multiplayer.Client.servCode;
import art.soft.multiplayer.ClientServer;
import static art.soft.multiplayer.ClientServer.CONECTION_ERROR;
import static art.soft.multiplayer.ClientServer.CONECTION_OK;
import static art.soft.multiplayer.ClientServer.CONECTION_WAIT;
import static art.soft.multiplayer.ClientServer.ConnectError;
import static art.soft.multiplayer.ClientServer.DISCONNECT_PLAYER;
import static art.soft.multiplayer.ClientServer.PLAYER_SET_INFO;
import static art.soft.multiplayer.ClientServer.SERVER_CANCELED;
import static art.soft.multiplayer.ClientServer.SERVER_PLAYING;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class PlayersField extends HScroll {
    
    public static Image Background, lock, unlock, kickOff;
    public static PlayersField first;
    public boolean isServer;
    public int distX, distY, teamY, imgW, butH, win[];
    public String locki, unlocki, kicki;
    public PlayersField next;
    
    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        if (kicki!=null) {
            kickOff = art.soft.utils.Image.loadImage(kicki);
            kicki = null;
        }
        if (locki!=null) {
            lock = art.soft.utils.Image.loadImage(locki);
            locki = null;
        }
        if (unlocki!=null) {
            unlock = art.soft.utils.Image.loadImage(unlocki);
            unlocki = null;
        }
        distX *= xk;
        distY *= yk;
        imgW *= xk;
        butH *= yk;
        if (first==null) first = this;
        else first.next = this;
        Background = im;
        im = null;
    }

    public static void connect() {
        PlayersField p = first;
        if (p!=null) {
            int n = 0;
            for (int i=ClientServer.teams.length-1; i>=0; i --) {
                if (ClientServer.teams[i].visible) n ++;
            }
            IButton b[] = new IButton[n];
            n --;
            for (int i=ClientServer.teams.length-1; i>=0; i --) {
                if (ClientServer.teams[i].visible) {
                    b[n --] = teamPool.createBut(p.x, i==0 ? 0 : p.teamY, p.width, p.distX, p.distY,
                            p.imgW, p.butH, p.color, p.fontSize, p.bold, ClientServer.teams[i]);
                }
            }
            do {
                p.buttons = b;
                p = p.next;
            } while (p!=null);
        }
    }

    public void toMainPage(boolean discon){
        if (discon) Game.disconnect();
        curWin.hideWin();
        Interface w = Interface.getWindow(win);
        if (w!=null) w.openWin();
    }
    
    @Override
    public void draw(art.soft.Graphics g){
        if (ConnectError!=CONECTION_WAIT) {
            active = true;
            if (!Game.mapInfo.connect) toMainPage(false); else
            if (ConnectError==CONECTION_ERROR) {
                System.out.println("Connect ERROR!!!");
                toMainPage(true);
            } else
            if (ConnectError==CONECTION_OK) {
                PlayerBut.isServer = isServer;
                if (!isServer) {
                    if (servCode==SERVER_PLAYING) { System.out.println("Server is busy!"); toMainPage(true); } else
                    if (servCode==SERVER_CANCELED) { System.out.println("Server is disconnect!"); toMainPage(true); } else
                    if (servCode==DISCONNECT_PLAYER) { System.out.println("Server kick-off you!"); toMainPage(true); } else
                    if (servCode>PLAYER_SET_INFO) {
                        toMainPage(false);
                        Game.reset = Game.RESET_START_GAME;
                    }
                }
                super.draw(g);
            }
        } else {
            active = false;
            drawText(g);
        }
    }

    @Override
    public void dispose(){
        super.dispose();
        first = null;
        if (lock!=null) { lock.dispose(); lock = null; }
        if (unlock!=null) { unlock.dispose(); unlock = null; }
        if (kickOff!=null) { kickOff.dispose(); kickOff = null; }
    }
}
