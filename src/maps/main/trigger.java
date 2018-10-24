package maps.main;

import art.soft.Game;
import static art.soft.Game.yourPlayer;
import art.soft.gui.Language;
import art.soft.multiplayer.Player;
import art.soft.multiplayer.Server;
import art.soft.specal.flyText;
import art.soft.triggers.gameTimer;
import art.soft.triggers.Trigger;
import art.soft.units.Unit;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Artem
 */
public class trigger extends Trigger {

    @Override
    public void mapInit(){
        Game.createUnit( 1500, 300, Game.uData[1], Game.yourPlayer );
        Game.createUnit( 1500, 1500, Game.uData[2], Game.players[10] );
        Unit l = Game.getLastUnit();
        l.canControl = -1;
        l.visGRP = -1;
        Game.createUnit( 1550, 1500, Game.uData[0], Game.players[10] );
        l = Game.getLastUnit();
        l.canControl = -1;
        l.visGRP = -1;
        /*Game.createUnit( 1500, 1500, 3, Game.players[ Server.NETRAL_ENEMY ] );
        Game.createUnit( 1540, 1530, 3, Game.players[ Server.NETRAL_ENEMY ] );
        Game.createUnit( 1480, 1540, 3, Game.players[ Server.NETRAL_ENEMY ] );
        Game.createUnit( 740, 1540, 1, Game.players[ Server.NETRAL_ENEMY ] );
        Game.createUnit( 860, 1540, 4, Game.players[ Server.NETRAL_ENEMY ] );*/
        //Game.createUnit( 300, 1500, 2, Game.yourPlayer );
        //Game.lastUnit.owners.addGroup( Game.CShost.allPlayer );
        
        gameTimer.startTimer( new spawnCreeps( 5000 ) );
        
        Game.curCam.moveCam( Game.yourPlayer.startX, Game.yourPlayer.startY );
    }
    
    @Override
    public void unitDie( Unit u, Unit killer ){
        for( int i=Server.ALL_PLAYER-1; i>=0; i-- ){
            Player tp = Game.players[ i ];
            if( tp.mainUnit==u ){
                int time = 2500 + u.uLvl.level * 50;
                gameTimer.startTimer( new unitRecive( tp, time ) );
            }
        }
        if (killer!=null) {
            //((flyText) specEffect.getEffect(flyText.class)).Init( killer, Color.RED, "Убил +1" );
            Player p = killer.getOwnerPlayer();
            if (p==yourPlayer) {
                ((flyText) Game.postEffects.getEffect(flyText.class)).Init(u, Color.YELLOW, "+45", u.visGRP | killer.visGRP );
                p.gold += 45;
            }
        }
    }
    
    @Override
    public void initPlayer( Player p ){
        int d = (p.ID & 3) * 50;
        p.startX = 200 + d;
        d = (p.ID >>> 2) * 50;
        p.startY = 1000 + d;
        p.gold = 0;
        p.gold_n = 90000;
        
        gameTimer.startTimer( new incPlayerGold( 600, p ) );
        
        Game.createUnit( p.startX, p.startY, Game.uData[0], p );
        p.mainUnit = Game.getLastUnit();
        p.gUnit.selectUnit( p.mainUnit, p.ID );
        for (int i=1; i>=0; i--) {
            Game.createUnit( p.startX+50, p.startY, Game.uData[2], p );
        }
        Game.postEffects.getEffect(flyText.class).Init( p.mainUnit, Color.YELLOW, Language.words.appear, p.mainUnit.visGRP );
    }
}
