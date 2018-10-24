package art.soft.items;

import art.soft.Game;
import static art.soft.items.Item.REMOVE_ITEM;
import static art.soft.items.ItemData.BUY_WITHOUT_RECURSEVE;
import art.soft.multiplayer.Player;
import art.soft.spells.Spell;
import art.soft.units.Unit;
import art.soft.units.UnitData;

/**
 *
 * @author Artem
 */
public class Shop {

    public static final int START_UNIT_PRIORETET = 10;
    public static final int ADD_SELECT_UNIT_PRIORETET = 1000;

    public int ID; // Индифекатор данной лавки
    public int numGUI; // Номер интерфейса который будет использован для текущего магазина
    public int shopGRP = -1; // группа игроков, которой доступна данная лавка

    public ShopItem items[];

    public int startItem[]; // начальный артефакт раздела
    public int numItems[]; // Количество артефактов в одном разделе

    public Shop Load(int id) {
        ID = id;
        for (int i = items.length-1; i>=0; i --) {
            items[i].Init(i, id);
        }
        startItem = new int[numItems.length];
        int j = 0;
        for (int i = 0; i < numItems.length; i ++) {
            startItem[i] = j;
            j += numItems[i];
        }
        return this;
    }

    public void playerBuyItem(Player p, int item) {
        if (p.owner.groupInGroup(shopGRP)) {
            ShopItem it = items[item];
            int id = ID;
            if (it.curShopID!=ID) ID = it.curShopID;
            int grp = p.owner.playerBit & shopGRP;
            Unit u = getNextUnit(grp);
            buyItemToUnit(p, it, u, grp);
            ID = id;
        }
    }

    protected void buyItemToUnit(Player p, ShopItem it, Unit u, int grp) {
        if (u!=null) {
            ItemData item = it.uData.itemData;
            if (item!=null && item.receptItems!=null && (item.attrib & BUY_WITHOUT_RECURSEVE)==0) {
                for (UnitData iud : item.receptItems) {
                    if (iud.shopData!=null && !u.haveItem(iud)) buyItemToUnit(p, iud.shopData, u, grp);
                }
            } else {
                int sID = it.curShopID;
                if (((1 << sID) & p.shopsMask)!=0) {
                    if (sID==ID) {
                        if (p.getGold()>=it.priceItem) {
                            int i = it.getAvailable(p);
                            if (i>0 && it.getCooldown(p)<=0) {
                                it.setAvailableItems(-- i, p);
                                it.startCooldown(p);
                                p.decGold(it.priceItem);
                                Unit t = it.getItem(p);
                                Spell s;
                                if (t.item!=null) {
                                    t.item.attrib |= REMOVE_ITEM;
                                    s = t.item.spell;
                                } else s = null;
                                Unit f = u;
                                while (u!=null) {
                                    if (u.giveItem(t)) return;
                                    u = getNextUnit(grp);
                                }
                                f.randomDropItem(t);
                            }
                        }
                    } else {
                        Shop s = getShop(sID);
                        if (s!=null) s.buyItemToUnit(p, it, u, grp);
                    }
                }
            }
        }
    }

    public Unit getNextUnit(int grp) {
        Unit u = null;
        int max = 0;
        Unit t = (Unit) Game.allUnits.first;
        while (t!=null) {
            if (t.shopID==ID && t.owners.groupInGroup(grp)) {
                int p = t.getShopPrior();
                if (max<p) {
                    max = p;
                    u = t;
                }
            }
            t = (Unit) t.next;
        }
        if (u!=null) {
            u.clearItemRemoveData();
            u.shopID = -1;
        }
        return u;
    }

    public static Shop getShop(int ID) {
        Shop s[] = Game.map.shops;
        if (s!=null && ID>=0 && ID<s.length) return s[ID];
        return null;
    }
}
