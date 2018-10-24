package art.soft.map;

/**
 *
 * @author Artem
 */
public class CommonMapData {

    public Decoration[] decors;
    public mapEffect effects[], animations[];
    public String GUIPath;

    public void copyToMap(GameMap map) {
        if (map.decors==null) map.decors = decors;
        if (map.effects==null) map.effects = effects;
        if (map.animations==null) map.animations = animations;
        if (map.GUIPath==null) map.GUIPath = GUIPath;
    }
}
