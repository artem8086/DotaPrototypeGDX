package art.soft.triggers;

import art.soft.multiplayer.*;
import art.soft.units.*;
import com.badlogic.gdx.maps.MapObject;

/**
 *
 * @author Артем
 */
public class Trigger {

    public MapObject regions[];

    public void mapInit(){}

    public void langReload(){}

    public void unitDie( Unit u, Unit killer ){}
    
    public void initPlayer( Player p ){}

    public void dispose(){}
}
