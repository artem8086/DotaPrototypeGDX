package art.soft.gui.buttons.menu;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.multiplayer.ClientServer;
import static art.soft.multiplayer.ClientServer.CONECTION_OK;

/**
 *
 * @author Artem
 */
public class StartGame extends BOpenI {
    
    @Override
    public void pressed(int code, int k){
        super.pressed(code, k);
        if (code==Interface.BUTTON_PRESSED && ClientServer.ConnectError==CONECTION_OK)
            Game.reset = Game.RESET_START_GAME;
    }
}
