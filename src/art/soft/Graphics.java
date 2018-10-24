package art.soft;

import art.soft.utils.GrayscaleShader;
import art.soft.utils.Image;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

/**
 *
 * @author Artem
 */
public class Graphics extends SpriteBatch {

    private static final Color color = new Color();
    private static final Rectangle scissors = new Rectangle();
    private static final Rectangle clipBounds = new Rectangle();
    private final ShapeRenderer sr;
    private BitmapFont font;
    private float xt, yt;
    private boolean drawShape;
    private Matrix4 matr;

    public Graphics(){
        super();
        sr = new ShapeRenderer();
        maxSpritesInBatch = 10000;
        drawShape = false;
    }

    @Override
    public void begin(){
        if (drawShape) {
            sr.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            drawShape = false;
        }
        super.begin();
    }

    @Override
    public void end(){
        if (drawShape) {
            sr.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            drawShape = false;
        } else
            super.end();
    }

    public void beginDraw(){
        if (drawShape) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
            super.begin();
            setProjectionMatrix(matr);
            drawShape = false;
        }
    }

    public void beginShape(ShapeType st){
        if (!drawShape) {
            matr = getProjectionMatrix();
            super.end();
            sr.setProjectionMatrix(matr);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            drawShape = true;
        }
        sr.begin(st);
   }

    public void setColor(int col){
        Color.argb8888ToColor(color, col ^ 0xFF000000);
        font.setColor(color);
        sr.setColor(color);
        //srCol.sub(color);
    }

    public void setStroke(int stroke) {
        Gdx.gl20.glLineWidth(stroke);
    }

    @Override
    public void setColor(Color col){
        sr.setColor(col);
        font.setColor(col);
    }

    @Override
    public Color getColor(){
        return sr.getColor();
    }

    public int stringWidth(String str){
        Game.layout.setText(font, str);
        return (int) Game.layout.width;
    }

    public int fontAscent(){
        return (int) font.getAscent();
    }

    public int fontDescent(){
        return (int) font.getDescent();
    }

    public int fontCapHeight(){
        return (int) font.getCapHeight();
    }

    public int fontHeight(){
        return (int) font.getLineHeight();
    }

    public void setFont(BitmapFont font){
        font.setColor(sr.getColor());
        this.font = font;
    }

    public float getX(int x){
        return x + xt;
    }

    public float getY(int y){
        return Game.h - y + yt;
    }

    public void drawString(String str, int x, int y){
        beginDraw();
        font.draw(this, str, x + xt, Game.h - y + yt);
    }

    public GlyphLayout drawString(String str, int x, int y, int width, int mode){
        beginDraw();
        return font.draw(this, str, x + xt, Game.h - y + yt, width, mode, false);
    }

    public GlyphLayout drawMultiLine(String str, int x, int y, int width, int mode){
        beginDraw();
        return font.draw(this, str, x + xt, Game.h - y + yt, width, mode, true);
    }

    public void draw(Image i, int x, int y, int w, int h){
        beginDraw();
        if (i.texture!=null)
            super.draw(i.texture, x + xt, Game.h - y - h + yt, w, h);
        else if (i.textureRegion!=null)
            super.draw(i.textureRegion, x + xt, Game.h - y - h + yt, w, h);
    }

    public void setAlpha(int col){
        Color.argb8888ToColor(color, col ^ 0xFF000000);
        super.setColor(color);
    }

    public void reset(){
        xt = yt = 0;
        setAlphaNormal();
    }

    public void translate(float x, float y){
        xt += x; yt -= y;
    }

    public void setAlpha(float alpha){
        Color c = super.getColor();
        setColor(c.r, c.g, c.b, alpha);
    }

    public void setAlphaNormal(){
        super.setColor(1, 1, 1, 1);
    }

    public void draw(Image i, int dx1, int dy1, int w, int h, int sx1, int sy1, int sx2, int sy2){
        beginDraw();
        super.draw(i.texture, dx1 + xt, Game.h - dy1 - h + yt, w, h, sx1, sy1, sx2 - sx1, sy2 - sy1, false, false);
    }

    public void draw(Image i, int dx1, int dy1, int w, int h, int sx1, int sy1, int sx2, int sy2, int origX, int origY, float rotate){
        beginDraw();
        super.draw(i.texture, dx1 + xt, Game.h - dy1 - h + yt, origX, origY, w, h, 1f, 1f, rotate, sx1, sy1, sx2 - sx1, sy2 - sy1, false, false);
    }

    public void drawRect(int x, int y, int width, int heigth){
        beginShape(ShapeType.Line);
        sr.rect(x + xt, Game.h - y - heigth + yt, width, heigth);
        sr.end();
    }

    public void drawLine(int x, int y, int x1, int y1){
        beginShape(ShapeType.Line);
        sr.line(x + xt, Game.h - y + yt, x1 + xt, Game.h - y1 + yt );
        sr.end();
    }

    public void fillRect(int x, int y, int width, int heigth){
        beginShape(ShapeType.Filled);
        sr.rect(x + xt, Game.h - y - heigth + yt, width, heigth);
        sr.end();
    }

    public void drawOval(int x, int y, int rx, int ry){
        beginShape(ShapeType.Line);
        sr.ellipse(x + xt,  Game.h - y - ry + yt, rx, ry);
        sr.end();
    }

    public void fillOval(int x, int y, int rx, int ry){
        beginShape(ShapeType.Filled);
        sr.ellipse(x + xt,  Game.h - y - ry + yt, rx, ry);
        sr.end();
    }

    public void fillPolygon(int x[], int y[], int n){
        Color c = sr.getColor();
        beginShape(ShapeType.Filled);
        ImmediateModeRenderer r = sr.getRenderer();
        float dy = Game.h + yt;
        float xc = x[0] + xt;
        float yc = dy - y[0];
        for (n --; n>1; n --) {
            r.color(c);
            r.vertex(x[n] + xt, dy - y[n], 0);
            r.color(c);
            r.vertex(x[n-1] + xt, dy - y[n-1], 0);
            r.color(c);
            r.vertex(xc, yc, 0);
        }
        sr.end();
    }

    public void setGray(boolean gray){
        setShader(gray ? GrayscaleShader.grayscaleShader : null);
    }

    public void setClip(int x, int y, int width, int heigth){
        flush();
        clipBounds.set((float) (x + xt), (float) (Game.h - y - heigth + yt), (float) width, (float) heigth);
        ScissorStack.calculateScissors(Game.hudCam, getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
    }

    public void endClip(){
        flush();
        ScissorStack.popScissors();
    }

    @Override
    public void dispose(){
        super.dispose();
        sr.dispose();
    }
}
