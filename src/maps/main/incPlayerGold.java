package maps.main;

import art.soft.multiplayer.Player;
import art.soft.triggers.gameTimer;

/**
 *
 * @author Artem
 */
public class incPlayerGold extends gameTimer{

    public Player pl;

    public incPlayerGold(int time, Player p) {
        super(time, true);
        pl = p;
    }

    @Override
    public void callTimer() {
        pl.gold ++;
    }
}
