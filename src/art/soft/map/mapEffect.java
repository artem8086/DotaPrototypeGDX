package art.soft.map;

import art.soft.Game;
import art.soft.specal.Simple;
import art.soft.specal.poolEffects;
import static art.soft.triggers.refreshEffects.miniMap;
import art.soft.units.AnimData;
import art.soft.units.Animation;

/**
 *
 * @author Artem
 */
public class mapEffect {

    public static final int EFFECT_MOVE_TO = 0;
    public static final int EFFECT_ATTACK = 1;
    public static final int EFFECT_MOVE_WAY = 2;
    public static final int EFFECT_ATTACK_WAY = 3;
    public static final int EFFECT_PING = 4;
    public static final int EFFECT_DANGER = 5;

    public AnimData aData;
    public int color = 0xFFFFFF, num;
    public float size = 1f, miniMapSize = 0;
    public boolean isCycle = false;
    public String anim;
    public Animation animation;

    public void Load() {
        if (anim!=null) {
            aData = AnimData.load(anim);
            anim = null;
        }
    }

    public void LoadAnim() {
        Load();
        animation = new Animation(aData);
        animation.setZoom(size);
        animation.setAnim(num);
        animation.isCycle = isCycle;
    }

    public void addEffect(int x, int y, int n, float size, boolean preOrPost, int vis) {
        poolEffects pe = preOrPost ? Game.preEffects : Game.postEffects;
        pe.getEffect(Simple.class).Init(aData, x, y, color, size * this.size, num, vis);
        if (miniMapSize!=0 && miniMap!=null) {
            miniMap.effects.getEffect(Simple.class).Init(aData,
                    miniMap.getMiniMapX(x), miniMap.getMiniMapY(y),
                    color, miniMapSize * this.size, num, vis);
        }
    }

    public void addEffect(int x, int y, int n, float size, int color, boolean preOrPost, int vis) {
        poolEffects pe = preOrPost ? Game.preEffects : Game.postEffects;
        pe.getEffect(Simple.class).Init(aData, x, y, color, size * this.size, num, vis);
        if (miniMapSize!=0 && miniMap!=null) {
            miniMap.effects.getEffect(Simple.class).Init(aData,
                    miniMap.getMiniMapX(x), miniMap.getMiniMapY(y),
                    color, miniMapSize * this.size, num, vis);
        }
    }

    public void dispose() {
        aData.dispose();
    }
}
