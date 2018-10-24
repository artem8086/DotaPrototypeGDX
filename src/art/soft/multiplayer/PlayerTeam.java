package art.soft.multiplayer;

import art.soft.Game;
import art.soft.utils.Image;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class PlayerTeam {

    public Image ic;
    public PlayerGroup team;
    public String nameK, icon;
    public boolean visible = true, auto = true;
    public int players, visGrp, freandGrp, color, numRes, num;

    public void Load(int i) {
        num = 0;
        int b = players;
        int n = 0;
        String name = Game.language.getText(nameK);
        team = new PlayerGroup(players, name);
        if (auto) visGrp = freandGrp = players;
        while (b!=0) {
            if ((b & 1)!=0) {
                num ++;
                Player p = ClientServer.players[n];
                p.team = this;
                p.teamInx = i;
                p.color = color;
                p.resorces = new int[numRes];
                p.inUse = !visible;
                p.player = visible;
                p.FrendPlayer.set(freandGrp, name);
                p.VisiblePlayers.set(visGrp, name);
                p.owner.set(players, name);
            }
            n ++;
            b >>>= 1;
        }
        if (icon!=null) {
            ic = Image.loadImage(icon);
            icon = null;
        }
    }

    public Player getPlayers() {
        Player ps = null;
        Player lp = null;
        int b = players;
        int n = 0;
        while (b!=0) {
            if ((b & 1)!=0) {
                Player p = ClientServer.players[n];
                if (ps==null) lp = ps = p;
                else {
                    lp.next = p;
                    lp = p;
                }
            }
            n ++;
            b >>>= 1;
        }
        if (lp!=null) lp.next = null;
        return ps;
    }

    public void dispose() {
        if (ic!=null) ic.dispose();
    }
}
