package art.soft.map;

import art.soft.Game;
import art.soft.gui.Language;
import art.soft.multiplayer.ClientServer;
import art.soft.multiplayer.PlayerTeam;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class mapConfig {

    public Image mapImage;
    public Language mapLang;

    public PlayerTeam[] teams;

    public boolean connect;
    public String version, author, name, description, Image, mapPath, mapName;

    public void Load() {
        connect = false;
        loadLang();
        if (Image!=null) {
            mapImage = art.soft.utils.Image.loadImage(GameMap.MAPS_PATH + Image);
            Image = null;
        }
    }

    public void loadLang() {
        if (mapLang!=null) {
            Language l = Game.json.fromJson(Language.class, Game.openFile(
                mapLang.getLocale(Game.settings.currentLang).path));
            mapLang.text = l.text;
        }
    }

    public void loadMap() {
        GameMap.Load(mapPath, mapName);
    }

    public void loadLobby() {
        connect = true;
        ClientServer.teams = teams;
        if (teams!=null)
        for (int i = teams.length-1; i>=0; i --) teams[i].Load(i);
    }

    public String getVersion() {
        return Language.words.version+Game.language.getText(version);
    }

    public String getAuthor() {
        return Language.words.author+Game.language.getText(author);
    }

    public String getName() {
        return Language.words.name+Game.language.getText(name);
    }

    public String getText() {
        return Game.language.getText(description);
    }

    public void dispose() {
        if (mapImage!=null) mapImage.dispose();
        for (PlayerTeam pt : teams) {
            pt.dispose();
        }
    }
}
