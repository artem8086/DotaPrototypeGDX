package art.soft.gui.buttons;

import art.soft.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 *
 * @author Artem
 */
public class EditBox extends TButton {

    private static final Drawable
            cursor = new TextureRegionDrawable(new TextureRegion(art.soft.utils.Image.loadImage("cursor.png").texture)),
            selection = new TextureRegionDrawable(new TextureRegion(art.soft.utils.Image.loadImage("selection.png").texture));

    private static final TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
    private static final Color col = new Color();

    public boolean border = true;
    public TextField tf;

    @Override
    public void load(double xk, double yk){
        super.load(xk, yk);
        tfs.cursor = cursor;
        tfs.selection = selection;
        tf = new TextField(text, tfs);
        Game.stage.addActor(tf);
        langReload();
    }

    @Override
    public void langReload(){
        super.langReload();
        tfs.font = font;
        //tfs.cursor = Game.curImage[0]
        Color.argb8888ToColor(col, color ^ 0xFF000000);
        tfs.fontColor = tfs.focusedFontColor = col;
        if (tf!=null) tf.setStyle(tfs);
    }

    @Override
    public void setText(String text) {
        this.text = text;
        if (tf!=null) tf.setText(text);
    }

    @Override
    public void draw(art.soft.Graphics g){
        //BitmapFont.BitmapFontData d = font.getData();
        //d.markupEnabled = false;
        if (border) {
            g.setColor(Color.WHITE);
            g.fillRect(x, y, width, height);
            g.setColor(color);
            x += 4;
            width -= 8;
        }
        tf.act(Game.deltaT);
        key = -1;
        tf.setPosition(g.getX(x), g.getY(y+height));
        tf.setSize(width, height);
        g.beginDraw();
        tf.draw(g, 1);
        text = tf.getText();
        if (border) {
            x -= 4;
            width += 8;
            g.setStroke(2);
            g.drawRect(x, y, width, height);
        }
        //d.markupEnabled = true;
    }
}
