package art.soft.specal;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.units.*;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Артем
 */
public class flyText extends specToUnit {

    public int textHWidth;
    public String text;
    public Color color;
    
    public void Init( int x, int y, Color col, String txt, int visible ){
        super.Init(x, y, 50, null, visible);
        color = col;
        setText(txt);
        setMovePos( x, y-100 );
    }

    public void setText(String text){
        this.text = text;
        if (text!=null) {
            Game.layout.setText(Game.getDefaultFont(), text);
            textHWidth = (int) Game.layout.width >> 1;
        }
    }

    public void Init( Unit u, Color col, String txt, int visible ){
        Init( u.getUnitXD(), u.getUnitYD()-u.getUnitHeight()-8, col, txt, visible );
    }

    @Override
    public boolean incAnim(){ return false; }

    @Override
    public void play( Graphics g, int x, int y ){
        g.setFont(Game.getDefaultFont());
        g.setColor(color);
        g.drawString( text, x - textHWidth, y );
    }
    
    @Override
    public void relese(){
        super.relese();
        text = null;
    }
}
