package maps.main;

import art.soft.Game;
import art.soft.triggers.gameTimer;
import art.soft.units.Unit;

/**
 *
 * @author Артем
 */
public class spawnCreeps extends gameTimer  {
    
    public int level = 1, t;
    
    public spawnCreeps( int time ){
        super( time, true );
    }
    
    @Override
    public void callTimer(){
        createCreep( 1600, 630, 2 );
        //createCreep( 1540, 630, 3 );
        //createCreep( 1480, 640, 3 );
        createCreep( 1600, 570, 1 );
        createCreep( 1500, 650, 1 );
        createCreep( 1650, 600, 1 );
        //createCreep( 1640, 590, 4 );
        t ++;
        if (t==4){ t = 0; level ++; } 
    }
    
    public void createCreep( int x, int y, int t ){
        Game.createUnit( x, y, Game.uData[t], Game.players[ 10 ] );
        Unit u = Game.getLastUnit();
        u.setAttackPos( 200, 1200, false );
        u.uLvl.setLevel(level);
        if (u.spells!=null) for (int i=6; i>=0; i--) {
                for (int j=0; j<u.spells.length; j++)
                    u.spells[j].incSpellLevel(u);
            }
    }
}
