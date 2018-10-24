package art.soft.gui;

import art.soft.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/**
 *
 * @author Артем
 */
public class Loading {

    public static void LoadScreenDraw(){
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        int w = Game.w >> 3;
        Game.g.begin();
        Game.g.draw(Game.Startup, (Game.w >> 1) - w, (Game.h >> 1) - w, w+w, w+w);
        Game.g.flush();
        Game.g.end();
    }
}
