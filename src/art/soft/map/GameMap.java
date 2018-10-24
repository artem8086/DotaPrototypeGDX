package art.soft.map;

import art.soft.Game;
import static art.soft.Game.json;
import art.soft.Graphics;
import art.soft.items.Shop;
import art.soft.multiplayer.ClientServer;
import art.soft.triggers.Trigger;
import art.soft.units.*;
import art.soft.utils.Image;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import art.soft.utils.Point;
import art.soft.utils.Rectangle;
import static art.soft.units.Unit.IS_ALIVE;
import static art.soft.units.UnitData.getUnitByName;
import art.soft.utils.TexturePacks;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;

/**
 *
 * @author Артем
 */
public class GameMap {

    public static final String MAPS_PATH = "maps/";
    public static final String COMMON_DATA = "common.json";

    public static final int CURRENT_GAME_VERSION = 1;
    public static final int CURRENT_MAP_VERSION = 1;

    public static Image miniMapImage;

    public static final int dxp = 32; // количество пикселей на одну ячейку
    public static final int dxw = 16; // количество пикселей на одну ячейку / 2
    public static final int dxs = 5;  // сдвиг на одну ячейку
    public static final int dxm = 31; // маска ячейки

    public static final int COLLISION_UNIT = 1; // колизия юнитов
    public static final int COLL_BUILDINGS = 2; // колизия зданий
    public static final int COLLIS_EARTH = 4; // колизия земли
    public static final int COLLISION_SKILL = 8; // колизия доп
    public static final int COLL_DECORATION = 16; // колизия декораций
    public static final int COLL_VISIBLE = 32; // колизия видимости
    
    public static final int VIS_AREA_DOWN = 8; // доп область видимсти в ячейках (снизу)
    public static final int VIS_AREA_LR = 6; // доп область видимсти в ячейках (слева/справа)

    public static Point tPoint = new Point();

    public Decoration[] decors; // decors - простые декорации, steps - декорации следов

    public int MAX_ATTACK_SPEED;
    public int MIN_ATTACK_SPEED;

    public int gameVersion;
    public int version;

    public int map[]; // Ячейки карты (4-байта на ячейку)
        // Формат ячейки:
        // 1 - байт:
            // 1 - бит: тип непроходимоти - юнит
            // 2 - бит: тип непроходимоти - строение
            // 3 - бит: тип непроходимоти - суша
            // 4 - бит: тип непроходимоти - доп непроходимость от заклинаний
            // 5 - бит: тип непроходимоти - декорация
            // 6 - бит: тип блокиратор видимости
            // 7-8 - биты: тип расположенного строения/декорации:
                // тип - 0: ничего нет
                // тип - 1: декорация
                // тип - 2: юнит, являющийся строением
                // тип - 3: временные декорации ( следы, фисура, ... )
        // 2 - байт:
            // высота блока от 0 до 255
        // 3-4 байты:
            // указывают ID юнита, декорации, ...

    public int txp = 32; // количество пикселей на один тайл
    public int txs = 5;  // сдвиг на один тайл
    public int tszx = 128; // количество тайлов по X

    public int typ = 16; // количество пикселей на одну ячейку
    public int tys = 4;  // сдвиг на один тайл
    public int tszy = 256; // количество тайлов по Y

    public int szp; // размер карты в пикселах x = y = zxp
    public int sz; // размер карты в ячейках
                //при этом sx всегда кратно степени 2-ки
                //то есть sx = sy = ..., 32, 64, 128, 512, ...
    public int ssz; // размер карты
                // sx = sy = 2 << ssx;

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    public String mapPath, miniMap, unitsData[], shopsData[], GUIPath;

    public Shop shops[];

    public Trigger trigger;

            // Текстурные атласы для иконок
    public TexturePacks textures[];

    public mapEffect effects[], animations[];

    public void addEffect(int x, int y, int n, float size, boolean preOrPost, int vis) {
        if (effects!=null && n<effects.length && effects[n]!=null) {
            effects[n].addEffect(x, y, n, size, preOrPost, vis);
        }
    }

    public void addEffect(int x, int y, int n, float size, int col, boolean preOrPost, int vis) {
        if (effects!=null && n<effects.length && effects[n]!=null) {
            effects[n].addEffect(x, y, n, size, col, preOrPost, vis);
        }
    }

    public Animation getAnimation(int n) {
        if (animations!=null && n<animations.length) {
            return animations[n].animation;
        }
        return null;
    }

    public Animation getAnimation(int n, int radius) {
        if (animations!=null && n<animations.length) {
            mapEffect a = animations[n];
            a.animation.setZoom(a.size * radius);
            return a.animation;
        }
        return null;
    }

    public static void Load(String name, String mapName){
        GameMap map = Game.json.fromJson(GameMap.class, Game.openFile(MAPS_PATH+name+"/"+mapName+".json"));
        if (map.gameVersion==CURRENT_GAME_VERSION && map.version<=CURRENT_MAP_VERSION) {
            Game.map = map;
            json.fromJson(CommonMapData.class, Game.openFile(MAPS_PATH+COMMON_DATA)).copyToMap(map);
                // Загрузка эффектов
            if (map.effects!=null) {
                for (mapEffect e : map.effects) {
                    e.Load();
                }
            }
            if (map.animations!=null) {
                for (mapEffect a : map.animations) {
                    a.LoadAnim();
                }
            }
                // Загрузка атласов
            if (map.textures!=null) {
                for (TexturePacks tp : map.textures) {
                    tp.Load();
                }
            }
                // Загрузка юнитов
            Game.uData = new UnitData[map.unitsData.length];
            for (int i=Game.uData.length-1; i>=0; i--) {
                Game.uData[i] = UnitData.loadUnitData(MAPS_PATH+map.unitsData[i], i);
            }
            for (UnitData ud : Game.uData) {
                ud.Init();
            }
            map.unitsData = null;
            
                // Загрузка лавок
            if (map.shopsData!=null) {
                map.shops = new Shop[map.shopsData.length];
                for (int i=map.shopsData.length-1; i>=0; i--) {
                    map.shops[i] = Game.json.fromJson(Shop.class,
                            Game.openFile(MAPS_PATH+map.shopsData[i]+".json")).Load(i);
                }
                map.shopsData = null;
            }
                // Загрузка декораций
            if (map.decors==null) map.decors = new Decoration[0];
            else for (Decoration d : map.decors) {
                d.Load();
                d.Init();
            }
                // Загрузка карты
            TiledMap tm = map.tiledMap = new TmxMapLoader().load(Game.dataDir+MAPS_PATH+map.mapPath);
            map.tiledMapRenderer = new OrthogonalTiledMapRenderer(map.tiledMap);
            miniMapImage = Image.loadImage(MAPS_PATH+map.miniMap);

            if (map.trigger==null) Game.trigger = new Trigger();
            else Game.trigger = map.trigger;

            map.map = new int[ map.sz << map.ssz ];
            for (MapLayer ml : tm.getLayers()) {
                String n = ml.getName();
                if (n.equals("regions")) {
                    MapObjects m = ml.getObjects();
                    int num = m.getCount();
                    Game.trigger.regions = new MapObject[num];
                    for (int i=num-1; i>=0; i--) {
                        MapObject mo = m.get(i);
                        if (mo instanceof RectangleMapObject) {
                            com.badlogic.gdx.math.Rectangle r = ((RectangleMapObject) mo).getRectangle();
                            r.y = map.szp - r.y - r.height;
                        } else if (mo instanceof CircleMapObject) {
                            com.badlogic.gdx.math.Circle c = ((CircleMapObject) mo).getCircle();
                            c.y = map.szp - c.y - c.radius;
                        } else if (mo instanceof EllipseMapObject) {
                            com.badlogic.gdx.math.Ellipse e = ((EllipseMapObject) mo).getEllipse();
                            e.y = map.szp - e.y - e.height;
                        }
                        Game.trigger.regions[i] = mo;
                    }
                } else if (n.equals("obstacles")) {
                    for (MapObject mo : ml.getObjects()) {
                        if (mo instanceof RectangleMapObject) {
                            com.badlogic.gdx.math.Rectangle r = ((RectangleMapObject) mo).getRectangle();
                            r.y = map.szp - r.y - r.height;
                            int x = (int) r.x >> dxs;
                            int y = (int) r.y >> dxs;
                            int w = (int) r.width >> dxs;
                            int h = (int) r.height >> dxs;
                            map.setRectMapMask(x, y, w, h, COLLIS_EARTH);
                        }
                    }
                } else if (n.equals("invisible")) {
                    for (MapObject mo : ml.getObjects()) {
                        if (mo instanceof RectangleMapObject) {
                            com.badlogic.gdx.math.Rectangle r = ((RectangleMapObject) mo).getRectangle();
                            r.y = map.szp - r.y - r.height;
                            int x = (int) r.x >> dxs;
                            int y = (int) r.y >> dxs;
                            int w = (int) r.width >> dxs;
                            int h = (int) r.height >> dxs;
                            map.setRectMapMask(x, y, w, h, COLL_VISIBLE);
                        }
                    }
                } else if (n.equals("landscape")) {
                    for (MapObject mo : ml.getObjects()) {
                        if (mo instanceof RectangleMapObject) {
                            int ht = (Integer.parseInt(mo.getProperties().get("height", String.class)) & 0xFF) << 8;
                            com.badlogic.gdx.math.Rectangle r = ((RectangleMapObject) mo).getRectangle();
                            r.y = map.szp - r.y - r.height;
                            int x = (int) r.x >> dxs;
                            int y = (int) r.y >> dxs;
                            int w = (int) r.width >> dxs;
                            int h = (int) r.height >> dxs;
                            map.clearRectMapMask(x, y, w, h, 0xFFFF00FF);
                            map.setRectMapMask(x, y, w, h, ht);
                        }
                    }
            } else if (n.equals("units")) {
                    for (MapObject mo : ml.getObjects()) {
                        if (mo instanceof RectangleMapObject) {
                            String u = mo.getProperties().get("unit", String.class);
                            int d = Integer.parseInt(mo.getProperties().get("dir", "0", String.class));
                            int o = Integer.parseInt(mo.getProperties().get("owner", "0", String.class));
                            int l = Integer.parseInt(mo.getProperties().get("level", "1", String.class));
                            com.badlogic.gdx.math.Rectangle r = ((RectangleMapObject) mo).getRectangle();
                            r.y = map.szp - r.y;
                            int x = (int) r.x;
                            int y = (int) r.y;
                            Unit su = Game.createUnit(x, y, getUnitByName(u), ClientServer.players[o]);
                            if (su!=null) {
                                su.uLvl.setLevel(l);
                                su.setDirection(d); 
                            }
                        }
                    }
                } else if (n.equals("decorations")) {
                    for (MapObject mo : ml.getObjects()) {
                        if (mo instanceof RectangleMapObject) {
                            int d = Integer.parseInt(mo.getProperties().get("decor", String.class));
                            com.badlogic.gdx.math.Rectangle r = ((RectangleMapObject) mo).getRectangle();
                            r.y = map.szp - r.y;
                            int x = (int) r.x >> dxs;
                            int y = (int) r.y >> dxs;
                            map.decors[d].setDecoration(x, y, d);
                        }
                    }
                }
            }
            map.mapPath = null;
            map.miniMap = null;
        }
    }

    public static void GDXrectangleToNormal(com.badlogic.gdx.math.Rectangle r) {
        r.y = Game.map.szp - r.y - r.height;
    }

    public int getSizePix(){
        return szp;
    }

    public void draw( Graphics g ){
        Rectangle r = Game.curCam.rect;
        //начало прорисовки фона тайтлов
        Game.curCam.translate(0, szp - Game.h);
        Game.curCam.update();
        tiledMapRenderer.setView(Game.curCam);
        tiledMapRenderer.render();
        Game.curCam.translate(0, - szp + Game.h);
        Game.curCam.update();
        //прорисовка пре-еффектов
        g.end();
        g.begin();
        Game.preEffects.refreshEffects(g);
        //начало прорисовки юнитов/декораций
        int sj = (r.x>>dxs)-VIS_AREA_LR;
        if( sj<0 ) sj = 0;
        int stx = sj<<dxs;
        int i = r.y>>dxs;
        int y = i<<dxs;
        int dt = ((r.width + r.x + dxp) >> dxs) + VIS_AREA_LR;
        int szx = sz > dt ? dt : sz;
        dt = ((r.height + r.y + dxp) >> dxs) + VIS_AREA_DOWN;
        int szy = sz > dt ? dt : sz;
        Unit p = Game.visUnits;
        int yp = szp;
        if( p!=null ) yp = p.py;
        Unit uh = null;
        Unit uh2 = Game.unitSelectNow;
        UnitGroupSelect ug = Game.yourPlayer.gUnit;
        int xm = Game.mRect.x;
        int ym = Game.mRect.y;

        if( i<0 ) i = 0;
        for( ; i<szy; i++ ){
            int x = stx;
            int k = (i<<ssz)+sj;
            for( int j=sj; j<szx; j++ ){
                int md = map[ k++ ];
                int h = (md>>8) & 255;
                switch( md & 0xC0 ){
                    case 0x40:
                        decors[ md>>>16 ].play( g, x, y-h );
                        g.end();
                        g.begin();
                        //g.fillRect( x, y, dxp, dxp );
                        break;
                    /*case 0x80:
                        Unit u = ((Unit)Game.getUnitByID( md>>>16 ));
                        if ((u.isVisible & Game.yourPlayerID) != 0) {
                            if( !Game.onGui && (u.status & IS_ALIVE)!=0 && u.rect.contains( xm, ym ) ) uh = u;
                            u.draw( g, u==uh2 );
                            //u.drawHP( g, false );
                        } else  u.draw( g, false );
                        break;*/
                }
                //g.setColor(0xFF0000);
                //if( (md & 31)!=0 ) g.fillRect( x, y, dxp, dxp );
                x += dxp;
            }
            y += dxp;
            if (yp<y) {
                while (p!=null) {
                    if (!Game.onGui && p.isContains(xm, ym)) uh = p;
                    p.draw(g, p==uh2);
                    if ((p.status & IS_ALIVE)!=0) ug.select( p );
                    p = p.VISnext;
                    if (p!=null) {
                        yp = p.py;
                        if (yp>=y) break;
                    }
                }
            }
        }
        Game.unitSelectNow = uh;

        Game.postEffects.refreshEffects(g);

        if (decors!=null)
        for( i=decors.length-1; i>=0; i-- )
            decors[i].incAnm();
        
        if (animations!=null) {
            for (mapEffect a : animations) {
                a.animation.incAnm();
            }
        }
        //for( i=steps.length-1; i>=0; i-- )
        //    steps[i].incAnm();
    }
    
    public int setRectMapMask( int x, int y, int w, int h, int m ){
        int szx = x + w;
        int szy = y + h;
        int max_h = 0;
        
        for( ; y<szy; y++ ){
            int j = x;
            int k = (y<<ssz)+j;
            for( ; j<szx; j++ ){
                h = (map[ k ] >> 8) & 255;
                if( h>max_h ) max_h = h;
                map[ k++ ] |= m;
            }
        }
        return max_h;
    }
    
    public void setMapMask( int x, int y, int m ){
        map[ (y<<ssz)+x ] |= m;
    }
    
    public int getMapMask( int x, int y ){
        return map[ (y<<ssz)+x ];
    }
    
    public void clearMapMask( int x, int y, int m ){
        map[ (y<<ssz)+x ] &= ~m;
    }
    
    public int canMove( int x, int y, int cMask, int z ){
        if (x<0 || x>=sz) return -1;
        if (y<0 || y>=sz) return -1;
        int t = map[ (y<<ssz)+x ];
        if( (t & cMask)==0 ){
            int h = (t >> 8) & 255;
            t = z - h;
            if( t < 0 ) t = -t; // t = abs(t);
            if( t <= Unit.MAX_HEIGHT_TO_GO ) return h;
        }
        return -1;
    }
    
    public void clearRectMapMask( int x, int y, int w, int h, int m ){
        m = ~ m;
        int szx = x + w;
        int szy = y + h;
        
        for( ; y<szy; y++ ){
            int j = x;
            int k = (y<<ssz)+j;
            for( ; j<szx; j++ ){
                map[ k++ ] &= m;
            }
        }
    }
    public boolean getCollRectMapMask( int x, int y, int dr, int m ){
        int szx = x + dr;
        int szy = y + dr;
        
        for( ; y<szy; y++ ){
            int j = x;
            int k = (y<<ssz)+j;
            for( ; j<szx; j++ ){
                if( (map[ k++ ] & m)!=0 ) return true;
            }
        }
        return false;
    }
    public boolean canSetPos( int xt, int yt, int r, int cType ){
        int dr = (r+r)>>dxs;
        if( (r & dxm)!=0 ) dr ++;
        int tx = (xt+dxw-r)>>dxs;
        int ty = (yt+dxw-r)>>dxs;
        if( getCollRectMapMask( tx, ty, dr, cType ) ){
            return false;
        }
        return true;
    }

    private static int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
        //возвращает 0, если аргумент (x) равен нулю; -1, если x < 0 и 1, если x > 0.
    }
    
    public static final int visMask = 0x30;    

    // функция ray-casting основаная на алгоритме Брезенхэма
    public boolean rayCastVis( int x1, int y1, int x2, int y2, int h, int uVis ){
        int dx, dy, incx, incy, pdx, pdy, es, el, err;

        x1 >>= dxs; x2 >>= dxs;
        y1 >>= dxs; y2 >>= dxs;
        //dx проекция на ось икс
        //dy проекция на ось игрек
        dx = x2 - x1;
        dy = y2 - y1;

        incx = sign(dx);
        incy = sign(dy);

        if (dx < 0) dx = -dx;//далее мы будем сравнивать: "if (dx < dy)"
        if (dy < 0) dy = -dy;//поэтому необходимо сделать dx = |dx|; dy = |dy|

        if (dx > dy) {
            pdx = incx;     pdy = 0;
            es = dy;        el = dx;
        } else {
            pdx = 0;        pdy = incy;
            es = dx;        el = dy;//тогда в цикле будем двигаться по y
        }
        err = el>>1;
 
        int mask = ~(sz-1);
	for( int t = 0; t < el; t++ ){//идём по всем точкам, начиная со второй и до последней
            err -= es;
            if( err<0 ){
                err += el;
                x1 += incx;//сдвинуть прямую (сместить вверх или вниз, если цикл проходит по иксам)
                y1 += incy;//или сместить влево-вправо, если цикл проходит по y
            } else {
                x1 += pdx;//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
                y1 += pdy;//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
            }
            if ((x1 & mask)!=0) return false;
            if ((y1 & mask)!=0) return false;
            int k = (y1<<ssz)+x1;
            int m = map[ k ];
            int v = (m & visMask);
            int dh = 0;
            if (v==COLL_DECORATION) {
                dh = decors[m>>>16].h;
            } else
            if (v!=0) return false;
            dh += (m>>8) & 0xFF;
            if ( (dh - h)>Unit.MAX_HEIGHT_TO_GO ) return false;
        }
        return true;
    }

    public Point teleportRayCast( int x2, int y2, int x1, int y1, int cMask ){
        int dx, dy, incx, incy, pdx, pdy, es, el, err;

        tPoint.setLocation(x2, y2);
        x1 >>= dxs; y1 >>= dxs;
        x2 >>= dxs; y2 >>= dxs;
        //dx проекция на ось икс
        //dy проекция на ось игрек
        dx = x2 - x1;
        dy = y2 - y1;

        incx = sign(dx);
        incy = sign(dy);

        if (dx < 0) dx = -dx;//далее мы будем сравнивать: "if (dx < dy)"
        if (dy < 0) dy = -dy;//поэтому необходимо сделать dx = |dx|; dy = |dy|

        if (dx > dy) {
            pdx = incx;     pdy = 0;
            es = dy;        el = dx;
        } else {
            pdx = 0;        pdy = incy;
            es = dx;        el = dy;//тогда в цикле будем двигаться по y
        }
        err = el>>1;
 
        int mask = ~(sz-1);
	for( int t = 0; t < el; t++ ){//идём по всем точкам, начиная со второй и до последней
            err -= es;
            if( err<0 ){
                err += el;
                x1 += incx;//сдвинуть прямую (сместить вверх или вниз, если цикл проходит по иксам)
                y1 += incy;//или сместить влево-вправо, если цикл проходит по y
            } else {
                x1 += pdx;//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
                y1 += pdy;//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
            }
            if( (map[ (y1<<ssz)+x1 ] & cMask)==0 ) {
                tPoint.setLocation((x1<<dxs)+dxw, (y1<<dxs)+dxw);
                return tPoint;
            }
        }
        return tPoint;
    }

    public void dispose(){
        if (miniMapImage!=null) miniMapImage.dispose();
        if (tiledMap!=null) tiledMap.dispose();
        if (decors!=null) for (Decoration d : decors) d.aData.dispose();
        if (effects!=null) for (mapEffect e : effects) e.dispose();
        if (animations!=null) for (mapEffect a : animations) a.dispose();
        if (textures!=null) for (TexturePacks t : textures) t.dispose();
    }
}
