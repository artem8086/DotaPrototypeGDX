package art.soft.items;

import art.soft.Game;
import art.soft.spells.Spell;
import art.soft.units.UnitData;
import art.soft.utils.StackObj;

/**
 *
 * @author Artem
 */
public class Item extends StackObj {

    public static int REMOVE_ITEM = 0x80000000;

    public ItemData iData;
    public int attrib; // атрибуты предмета
    public Spell spell;

    public Item() {}

    public Item(ItemData iData) {
        setData(iData);
    }

    public final Item setData(ItemData id) {
        iData = id;
        attrib = id.attrib;
        if (id.spell!=null) {
            spell = id.spell.copy();
        }
        return this;
    }

    @Override
    public void relese() {
        iData = null;
        if (spell!=null) {
            spell.relese();
            spell = null;
        }
        Game.poolItems.add(this);
    }

    public boolean isEqualsItems(UnitData items[]) {
        UnitData uData = iData.uData;
        for (UnitData ud : items) {
            if (ud==uData) return true;
        }
        return false;
    }
}
