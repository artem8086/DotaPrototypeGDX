package art.soft.items;

import art.soft.multiplayer.ClientServer;
import art.soft.multiplayer.Player;
import art.soft.multiplayer.PlayerGroup;
import art.soft.triggers.gameTimer;
import art.soft.units.Unit;
import art.soft.units.UnitData;
import static art.soft.units.UnitData.getUnitByName;

/**
 *
 * @author Artem
 */
public class ShopItem {

    public UnitData uData;
    public int ID, shopID, curShopID = -1;
    public String name;

    private long cTimeEndAll;
    private dependencesFromPlayer dfp[]; // кулдауны для каждого игрока или для каждой команды,
            // в зависимости от владельца

    // Данные для магазина
    public int priceItem, cooldown;
    public int maxItemInShop = 1;
    public int availableItems = 1;
    public int itemsGroup = 1; // Общий доступ у
            // 0 - игрок который купил
            // 1 - команда игрока
            // иначе - все игроки

    public int itemsOwner; // Владелец при покупке:
            // 0 - игрок который купил
            // 1 - команда игрока
            // 2 - все игроки
            // иначе никому не принадлежит

    private class dependencesFromPlayer {
        long cTimeEnd;
        int availableItems;

        dependencesFromPlayer(int aIt, long cd) {
            availableItems = aIt;
            cTimeEnd = aIt<0 ? cd : 0;
        }
    }

    public void Init(int id, int shopID) {
        if (curShopID<0) curShopID = shopID;
        this.shopID = shopID;
        ID = id;
        if (name!=null) {
            if ((uData = getUnitByName(name))!=null)
                uData.shopData = this;
            name = null;
        }
        if (cooldown>0) {
            if (itemsGroup==0) dfp = new dependencesFromPlayer[ClientServer.players.length]; else
            if (itemsGroup==1) dfp = new dependencesFromPlayer[ClientServer.teams.length];
            cTimeEndAll = gameTimer.gameTime;
            if (availableItems<=0) cTimeEndAll += cooldown;
            for (int i=dfp.length-1; i>=0; i --) {
                dfp[i] = new dependencesFromPlayer(availableItems, cTimeEndAll);
            }
        }
    }

    public void setTimeEnd(long time, Player p) {
        if (dfp!=null) {
            if (itemsGroup==0) dfp[p.ID].cTimeEnd = time; else
            if (itemsGroup==1) dfp[p.teamInx].cTimeEnd = time;
        } else
            cTimeEndAll = time;
    }

    public long getTimeEnd(Player p) {
        if (dfp!=null) {
            if (itemsGroup==0) return dfp[p.ID].cTimeEnd; else
            if (itemsGroup==1) return dfp[p.teamInx].cTimeEnd;
        }
        return cTimeEndAll;
    }

    public void setAvailableItems(int itn, Player p) {
        if (dfp!=null) {
            if (itemsGroup==0) dfp[p.ID].availableItems = itn; else
            if (itemsGroup==1) dfp[p.teamInx].availableItems = itn;
        } else
            availableItems = itn;
    }

    public int getAvailableItems(Player p) {
        if (dfp!=null) {
            if (itemsGroup==0) return dfp[p.ID].availableItems; else
            if (itemsGroup==1) return dfp[p.teamInx].availableItems;
        }
        return availableItems;
    }

    //public void 
    public int getAvailable(Player p){
        if (cooldown<=0) return availableItems = maxItemInShop;
        else {
            long te = getTimeEnd(p);
            long time = gameTimer.gameTime - te;
            int i = getAvailableItems(p);
            if (time > 0) {
                int n = (int) (time / cooldown) + 1;
                i += n;
                if (i>=maxItemInShop) {
                    i = maxItemInShop;
                    setTimeEnd(gameTimer.gameTime, p);
                } else {
                    if (i<=0) setTimeEnd(n * cooldown + te, p);
                }
                setAvailableItems(i, p);
            }
            return i;
        }
    }

    public Unit getItem(Player p) {
        PlayerGroup pg = ClientServer.emptyGroup;
        if (itemsOwner==0) pg = p.owner; else
        if (itemsOwner==1) pg = p.team.team; else
        if (itemsOwner==2) pg = ClientServer.allPlayer;
        Unit u = uData.createUnit(pg);
        return u;
    }

    public int getCooldown(Player p) {
        return (int) (getTimeEnd(p) - gameTimer.gameTime);
    }

    public void startCooldown(Player p){
        if (cooldown>0) setTimeEnd(gameTimer.gameTime + cooldown, p);
    }
}
