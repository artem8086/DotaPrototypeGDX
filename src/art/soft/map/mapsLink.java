package art.soft.map;

import art.soft.Game;
import art.soft.utils.Image;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class mapsLink {

    public Image smal;
    public String imageSmal, mapName, mapConfig;

    public mapsLink[] maps;

    public void Load() {
        if (imageSmal!=null) {
            smal = Image.loadImage(GameMap.MAPS_PATH+imageSmal);
            imageSmal = null;
        }
    }
}
