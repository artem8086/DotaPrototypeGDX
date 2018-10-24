package art.soft.map;

import art.soft.Game;
import art.soft.utils.Point;
import art.soft.utils.Rectangle;

/**
 *
 * @author Артем
 */
public class GameCamera extends com.badlogic.gdx.graphics.OrthographicCamera {

public Rectangle rect, mapRect, mRect, scrRect;
public Point dop1, dop2;
public float x, y;
public float ds, qs, vx, vy; // скорость передвижения
public int dir, yt, xt;
public float zoomSpeed, zoomMax, zoomMin;

public GameCamera( int sz ){
    super(Game.w, Game.h);
    mapRect = new Rectangle( 0, 0, sz, sz );
    rect = new Rectangle(0, 0, Game.w, Game.h);
    scrRect = new Rectangle();
    mRect = new Rectangle();
    dop1 = new Point();
    dop2 = new Point();
    xt = Game.w >> 1;
    yt = Game.h >> 1;
    translate(xt, yt);
    xt = yt = 0;
    zoomMax = 1.4f;
    zoomMin = 0.8f;
}

public void setZoomSpeed( float speed ){
    zoomSpeed = speed;
}

public void Init(){
    setSpeed( 700 );
}

public void setSpeed( int speed ){
    ds = speed;
    qs = speed * 0.70710678118654752440084436210485f; // cos( 45 ) = 0,707
}

public void resize(){
    rect.width = Game.w;
    rect.height = Game.h;
    setXY();
}

public void setXY(){
    int ds = mapRect.width-rect.width;
    if( rect.x>ds ){ x = rect.x = ds; }
    ds = mapRect.height-rect.height;
    if( rect.y>ds ){ y = rect.y = ds; }
    if( rect.x<mapRect.x ){ x = rect.x = mapRect.x; }
    if( rect.y<mapRect.y ){ y = rect.y = mapRect.y; }
    translate( rect.x - xt, yt - rect.y );
    xt = rect.x; yt = rect.y;
}

public void moveCam( int dx, int dy ){
    dx -= rect.width >> 1;
    dy -= rect.height >> 1;
    rect.x = dx; rect.y = dy;
    x = dx; y = dy;
    setXY();
}

public void move(){
    x += vx * Game.deltaT;
    y += vy * Game.deltaT;
    rect.x = (int) x;
    rect.y = (int) y;
    setXY();
    update();
}

public static final int MOUSE_DEFECT = 16; // разброс мыши

public Point getPos( int x, int y){
    dop1.setLocation( x+rect.x, y+rect.y );
    return dop1;
}

public Rectangle getMousePos( int x1m, int y1m, int x2m, int y2m ){
    dop1.setLocation( x1m+rect.x, y1m+rect.y );
    dop2.setLocation( x2m+rect.x, y2m+rect.y );
    mRect.setFrameFromDiagonal( dop1, dop2 );
    if( mRect.width<MOUSE_DEFECT && mRect.height<MOUSE_DEFECT ){
        mRect.width = 0;
        mRect.height = 0;
    }
    scrRect.setFrameFromDiagonal( x1m, y1m, x2m, y2m );
    return mRect;
}

public void setDir( int dir ){
    if( this.dir!=dir && dir>=0 ){
        this.dir = dir;
        switch( dir ){
            case 0: vx = 0; vy = 0; break; // не двигатся
            case 1: vx = 0; vy = -ds; break; // вверх
            case 2: vx = qs; vy = -qs; break; // вверх и впаво
            case 3: vx = ds; vy = 0; break; // вправо
            case 4: vx = qs; vy = qs; break; // вправо и вниз
            case 5: vx = 0; vy = ds; break; // вниз
            case 6: vx = -qs; vy = qs; break; // вниз и влево
            case 7: vx = -ds; vy = 0; break; // влево
            case 8: vx = -qs; vy = -qs; break; // влево и вверх
        }
    }
    move();
}

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void update(boolean updateFrustum) {
        super.update(updateFrustum);
    }

}
