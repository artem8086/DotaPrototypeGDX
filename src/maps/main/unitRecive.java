package maps.main;

import art.soft.Game;
import art.soft.multiplayer.*;
import art.soft.triggers.gameTimer;

/**
 *
 * @author Артем
 */
public class unitRecive extends gameTimer {
    
    Player p;
    
    public unitRecive( Player p, int time ){
        super( time, false );
        this.p = p;
    }
    
    @Override
    public void callTimer(){
        if( p.mainUnit!=null ){
            Game.backToLife( p.mainUnit );
            p.mainUnit.setPos(p.startX, p.startY);//teleport( p.startX, p.startY );
        }
    }
}
