package art.soft.gui.buttons.settings;

import art.soft.gui.buttons.TButton;

/**
 *
 * @author Artem
 */
public class SetBut extends TButton {

    public boolean mover = false;
    private static final int col1 = 0x404040;
    private static final int col2 = 0x202020;
    private static final int col3 = 0x909090;

    @Override
    public void draw(art.soft.Graphics g){
        g.setColor(mover ? col1 : col2);
        g.fillRect(x, y, width, height);
        g.setColor(col3);
        g.setStroke(2);
        g.drawRect(x, y, width, height);
        super.draw(g);
        mover = false;
    }

    @Override
    public void pressed(int code, int k){
        mover = true;
    }
}
