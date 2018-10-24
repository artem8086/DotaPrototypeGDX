package art.soft.gui;

import art.soft.Game;

/**
 *
 * @author Artem
 */
public class inGameWords {

    public String miss, passive_netral, enemy_netral, die, appear,
            allPlayers, locked, openSlot, font, version, author, name;

    public String missK, passive_netralK, enemy_netralK, dieK, appearK,
            allPlayersK, lockedK, openSlotK, fontK, versionK, authorK, nameK;

    public void Load(){
        miss = Game.language.getText(missK);
        passive_netral = Game.language.getText(passive_netralK);
        enemy_netral = Game.language.getText(enemy_netralK);
        die = Game.language.getText(dieK);
        appear = Game.language.getText(appearK);
        allPlayers = Game.language.getText(allPlayersK);
        locked = Game.language.getText(lockedK);
        openSlot = Game.language.getText(openSlotK);
        font = Game.language.getText(fontK);
        version = Game.language.getText(versionK);
        author = Game.language.getText(authorK);
        name = Game.language.getText(nameK);
    }
}
