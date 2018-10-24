package art.soft.gui.buttons.test;

import art.soft.Graphics;
import art.soft.gui.buttons.DragTarget;
import art.soft.gui.buttons.DraggedBut;
import art.soft.gui.buttons.menu.fillBut;

/**
 *
 * @author Artem
 */
public class dropTest extends fillBut implements DragTarget {

    private int colOver;
    
    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        colOver = color;
        color = -1;
    }
    
    @Override
    public void draw(Graphics g) {
        super.draw(g);
        color = -1;
    }
    
    @Override
    public void draggedOver(DraggedBut target) {
        color = colOver;
    }

    @Override
    public void DropIn(DraggedBut target) {
        System.out.println("Drop "+target);
    }
}
