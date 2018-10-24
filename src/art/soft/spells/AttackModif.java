package art.soft.spells;

import static art.soft.multiplayer.ClientServer.random;
import art.soft.units.Unit;
import art.soft.utils.Image;

/**
 *
 * @author Artem
 */
public class AttackModif extends ItemStatsAdd {

    public int chance = 100;
    public Effect effect;
    protected boolean addModif;

    @Override
    public void Load(){
        super.Load();
        effect.Load();
    }

    @Override
    public void setOwner(Unit u){
        if (effect!=null) effect.setOwner(u);
        super.setOwner(u);
    }

    @Override
    public Effect copy(Effect e){
        AttackModif ef = (AttackModif) super.copy(e);
        ef.effect = effect;
        ef.chance = chance;
        return ef;
    }

    @Override
    public Image getIcon(){
        return effect.Icon;
    }

    @Override
    public int startDamageAim(Unit u, Unit aim){
        addModif = random.nextBoolean(chance);
        return addModif ? modif : 0;
    }

    @Override
    public int attackDamageAim(Unit u, Unit aim){
        return addModif ? modif : 0;
    }

    @Override
    public Effect get2Modificator(){
        return addModif ? effect : null;
    }
}
