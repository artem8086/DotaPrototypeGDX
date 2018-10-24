package art.soft.multiplayer;

import art.soft.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 *
 * @author Artem
 */
public class Profile {
    
    private static final String profile_file = "profile.json";

    public String name;
    
    public static Profile Load(){
        Profile profile = null;
        FileHandle p = Gdx.files.local(profile_file);
        if (p.exists())
            profile = Game.json.fromJson(Profile.class, p);
        else profile = new Profile();
        return profile;
    }

    public static void Save(Profile profile){
        Game.json.toJson(profile, Gdx.files.local(profile_file));
    }
}
