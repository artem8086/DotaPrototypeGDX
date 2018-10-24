package art.soft.gui.buttons.menu;

import art.soft.gui.buttons.TButton;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Artem
 */
public class yourIP extends TButton {

    @Override
    public void draw(art.soft.Graphics g){
        try {
            setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            text = null;
        }
        super.draw(g);
    }
}
