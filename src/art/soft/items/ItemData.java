package art.soft.items;

import art.soft.Game;
import art.soft.spells.Spell;
import art.soft.units.UnitData;
import static art.soft.units.UnitData.getUnitByName;

/**
 *
 * @author Artem
 */
public class ItemData {

        // Атребуты артефакта
    public static final int ITEM_SHOW_NUMBER = 1;
    public static final int ITEM_CANT_BE_DROPED = 2;
    public static final int ITEM_DROP_WHEN_DIE = 4;
    public static final int ITEM_IN_ONE_SLOT = 8;
    public static final int BUY_WITHOUT_RECURSEVE = 0x10;
    //public static final int ITEM_CAN_DISASSEMBLE = 0x20;

    public UnitData[] nextItems;
    public UnitData[] receptItems;
    public UnitData[] commonCooldown;
    public String[] nextItemsN;
    public String[] receptItemsN;
    public String[] commonCooldownN;
    //public String itemName;
    public UnitData uData;
    public Spell spell;
    public int attrib; // атрибуты предмета
    public int maxItemsInSlot = -1; // -1 - неограниченое количество
    public int showNextItemsNum, showReceptItemsNum;


    public void Init(UnitData uData) {
        this.uData = uData;
        if (nextItemsN!=null) {
            nextItems = new UnitData[nextItemsN.length];
            for (int i=nextItems.length-1; i>=0; i --) {
                nextItems[i] = getUnitByName(nextItemsN[i]);
            }
            nextItemsN = null;
        }
        if (receptItemsN!=null) {
            receptItems = new UnitData[receptItemsN.length];
            for (int i=receptItems.length-1; i>=0; i --) {
                receptItems[i] = getUnitByName(receptItemsN[i]);
            }
            receptItemsN = null;
        }
        if (commonCooldownN!=null) {
            commonCooldown = new UnitData[commonCooldownN.length];
            for (int i=commonCooldown.length-1; i>=0; i --) {
                commonCooldown[i] = getUnitByName(commonCooldownN[i]);
            }
            commonCooldownN = null;
        }
        if (spell!=null) spell.preLoad();
    }

    public void postLoad() {
        if (spell!=null) spell.postLoad();
    }

    public Item getItem() {
        return Game.poolItems.getFirst(Item.class).setData(this);
    }

    public void dispose() {
        if (spell!=null) spell.dispose();
    }
}
