package art.soft.multiplayer;

/**
 *
 * @author Артем
 */
public class PlayerGroup {

    public static int yourPlayerBit;

    public int playerBit; // номер бита указывает на ID игрока
                // 1 - игрок присутствует в группе
                // 0 - игрок отсутствует в группе
    public String name;

    public PlayerGroup( int bit, String name ){
        playerBit = bit;
        this.name = name;
    }

    public void set(int bit, String name) {
        playerBit = bit;
        this.name = name;
    }

    public void addPlayer( int id ){
        playerBit |= 1 << id;
    }

    public void delPlayer( int id ){
        playerBit &= (-1) ^ (1 << id);
    }

    public void addGroup( PlayerGroup gr ){
        playerBit |= gr.playerBit;
    }

    public void delGroup( PlayerGroup gr ){
        playerBit &= (-1) ^ gr.playerBit;
    }

    public void setPlayer( Player p ){
        if( p!=null ){
            playerBit = 1 << p.ID;
            name = p.name;
        }
    }

    public boolean inGroup( int id ){
        return (playerBit & (1 << id)) != 0;
    }

    public boolean youInGroup(){
        return (playerBit & yourPlayerBit) != 0;
    }

    public boolean groupInGroup( PlayerGroup pg ){
        return (playerBit & pg.playerBit) != 0;
    }

    public boolean groupInGroup( int groupBit ){
        return (playerBit & groupBit) != 0;
    }

    public Player getPlayers() {
        Player ps = null;
        Player lp = null;
        int b = playerBit;
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

    public void setShop(int shopID, int p) {
        int b = playerBit;
        Player pl;
        int i = 0;
        while (b!=0) {
            if ((b & 1)!=0) {
                pl = ClientServer.players[i];
                if (pl!=null) {
                    pl.shopsMask |= 1 << shopID;
                    if (pl.lastPrior<=p) {
                        pl.lastPrior = p;
                        pl.curShop = shopID;
                    }
                }
            }
            b >>>= 1;
            i ++;
        }
    }
}
