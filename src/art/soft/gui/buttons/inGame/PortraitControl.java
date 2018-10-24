package art.soft.gui.buttons.inGame;

import art.soft.Game;
import art.soft.gui.Interface;
import art.soft.gui.buttons.IButton;
import art.soft.spells.Spell;
import art.soft.units.Unit;

/**
 *
 * @author Artem
 */
public class PortraitControl extends IButton {
 
    @Override
    public void pressed(int code, int k){
        if (code==Interface.BUTTON_PRESSED) {
            Unit u = Game.yourPlayer.gUnit.selUnit;
            if (u!=null) {
                if (Game.statusKey==Game.DO_ATTACK)
                    Game.CShost.setAttackUnit(u);
                if (Game.statusKey==Game.DO_SELECT_SPELL) {
                    if ((Game.castSpell.attrib & Spell.CAST_ON_UNIT)!=0 &&
                        (Game.castSpell.influents & Spell.YOUR_AURA)!=0)
                        Game.CShost.castSpellOnUnit(Game.spellNum, u);
                } else {
                    Game.CShost.selectUnit(u);
                    Game.curCam.moveCam(u.px, u.py);
                }
            }
        }
        if (code!=Interface.MOUSE_OVER) Game.statusKey = Game.DO_NOTHING;
    }
}
