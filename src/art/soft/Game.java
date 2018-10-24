package art.soft;

import art.soft.utils.Rectangle;
import art.soft.utils.Fonts;
import art.soft.triggers.gameTimer;
import static art.soft.Game.Intro;
import art.soft.gui.*;
import static art.soft.gui.buttons.inGame.GameControl.render;
import art.soft.gui.buttons.menu.PlayersField;
import art.soft.multiplayer.*;
import art.soft.specal.*;
import art.soft.units.*;
import art.soft.map.*;
import art.soft.spells.Spell;
import art.soft.triggers.refreshEffects;
import art.soft.triggers.Trigger;
import static art.soft.units.Unit.IS_ALIVE;
import art.soft.utils.ScreenshotFactory;
import art.soft.utils.Stack;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import art.soft.utils.Image;
import art.soft.utils.TexturePacks;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import java.net.*;

public class Game implements ApplicationListener, InputProcessor {

    public static final String dataDir = "data/";

    public static int w, h;
    
    public static final int MAX_UNIT = 65536;
    public static Unit visUnits;// bldUnits, movUnits;
    public static Stack allUnits = new Stack(), poolUnit = new Stack();
    public static Stack poolCreep = new Stack(), poolHero = new Stack();
    public static Stack poolItems = new Stack();

    public static Unit[] AllUnitsID;
    public static int numUnit, lastID;

    public static int circleX, circleY, circleR; // Круг, который рисуется
                    // для отображения радиуса ауры

    public static boolean secondTick = false;
    public static final int gameCycle = 100, gCYC = 10; // игровой цикл каждые 100 мс, то есть 10 раз в секунду
    public static final float gCYCo = 0.1f;
    public static float deltaT;
    public static int tFrame;

    public static Player[] players;
    public static Player yourPlayer;
    public static int yourPlayerID;
    public static Unit unitSelectNow;

    public static UnitData[] uData;

    public static Pixmap[] curImage;
    public static int curCur;

    public static intro Intro;

    public static final boolean fogON = false;

    public static GameMap map;
    public static mapConfig mapInfo, mainMap;
    public static Trigger trigger;
    public static poolEffects preEffects, postEffects;
    public static GameCamera curCam, mainCam;
    public static OrthographicCamera hudCam;
    public static Rectangle mRect;

    public static ClientServer CShost;
    public static int port;
    public static InetAddress host;
    public static String portT, hostT;

    public static Fonts fonts;
    public static GlyphLayout layout = new GlyphLayout();

    public static int xm, ym, xsd, ysd;

    public static boolean onGui = true;
    public static Interface HUD, inGame, Menu;
    public static Image ringAOE;

    public static Stage stage;

    public static final int RESET_NOT        = 0;
    public static final int RESET_LANGUAGE   = 1;
    public static final int RESET_START_GAME = 2;
    public static final int RESET_SETTINGS   = 3;
    public static final int RESET_LOAD       = 4;

    public static final int DO_NOTHING      = 0;
    public static final int DO_ATTACK       = 1;
    public static final int DO_LEVEL_UP     = 2;
    public static final int DO_SELECT_SPELL = 3;
    public static final int DO_SELECT_ITEM  = 4;
    public static final int DO_SELECT_UNITS = 5;

    public static boolean mbut1, mbut2, m_drag, but1Up, but2Up;
    public static boolean control, shift, alt;
    public static boolean up, down, left, right, getInfo;
    public static boolean chooseMainUnit, initGame;
    public static int keyPressed, keyRelessed, statusKey, reset, spellNum, mWheel;
    public static Spell castSpell;

    public static Profile profile;
    public static Settings settings;

    public static Language language;

    public static Json json = new Json();

    public static Image Startup;
    public static Graphics g;

    public static Game game;

    public static FileHandle openFile( String file ){
        return Gdx.files.internal(dataDir + file);
    }

    public static Pixmap loadPixmap(String im){
        return new Pixmap(Gdx.files.internal(dataDir + im));
    }

    public static final int CURSOR_DEFAULT          = 0;
    public static final int CURSOR_ATTACK           = 1;
    public static final int CURSOR_LEARN_ABILITY    = 2;
    public static final int CURSOR_DEFAULT_ENEMY    = 3;
    public static final int CURSOR_DEFAULT_TEAM     = 4;
    public static final int CURSOR_ATTACK_TEAM      = 5;
    public static final int CURSOR_ATTACK_ILLEGAL   = 6;
    public static final int CURSOR_SPELL_DEFAULT    = 7;
    public static final int CURSOR_SPELL_ILLEGAL    = 8;
    public static final int CURSOR_SPELL_WALK_TO    = 9;

    public static final int NUM_CURSORS             = 10;
    public static final int NUM_TOP_LEFT_CURSORS    = 7;


    private void loadCursors( String dir ){
        curImage = new Pixmap[NUM_CURSORS];
        curImage[0] = loadPixmap(dir+"default.png");
        curImage[1] = loadPixmap(dir+"attack.png");
        curImage[2] = loadPixmap(dir+"learn_ability.png");
        curImage[3] = loadPixmap(dir+"default_enemy.png");
        curImage[4] = loadPixmap(dir+"default_team.png");
        curImage[5] = loadPixmap(dir+"attack_team.png");
        curImage[6] = loadPixmap(dir+"attack_illegal.png");
        curImage[7] = loadPixmap(dir+"spell_default.png");
        curImage[8] = loadPixmap(dir+"spell_illegal.png");
        curImage[9] = loadPixmap(dir+"spell_walkto.png");
	Gdx.input.setCursorImage(curImage[0], 0, 0);
        curCur = 0;
    }

    public static void GUILoad(boolean state){
        if (Menu!=null) Menu.dispose();
        if (inGame!=null) inGame.dispose();

        Interface g = null;
        if (map!=null && map.GUIPath!=null) {
            g = json.fromJson(Interface.class, openFile(map.GUIPath));
            g.load();
        }

        Interface m = json.fromJson(Interface.class, openFile("menu/menu.json"));
        m.load();

        if (state) {
            HUD = HUD==Menu ? m : g;
            Menu.copyState(m);
            if (g!=null && inGame!=null) inGame.copyState(g);
        } else
            HUD = m;
        inGame = g;
        Menu = m;
    }

    public void Load(){
        System.setProperty("file.encoding", "UTF-8");
        loadCursors( "cursor/" );

        profile = Profile.Load();
        language = Language.Load();
        settings.apply();

        hudCam = new OrthographicCamera(w, h);
        hudCam.translate(Game.w >>> 1, Game.h >>> 1);
        hudCam.update();
        stage = new Stage(new ScalingViewport(Scaling.stretch, w, h, hudCam), g);

        ClientServer.Init();

        GUILoad(false);

        mainMap = json.fromJson(mapConfig.class, Game.openFile(GameMap.MAPS_PATH+"main/dota_cfg.json"));
        mainMap.Load();

        ringAOE = Image.loadImage("gui/ring_aoe.png");

        System.gc();
    }

    public static void Init() {
        System.out.println("\nStart new game!");

        // Скрываем окно запуска игры
        Menu.getWindow(1).windows.show = false;

        while (ClientServer.ConnectError!=ClientServer.CONECTION_OK) {
            if (ClientServer.ConnectError==ClientServer.CONECTION_ERROR) {
                System.out.println("Connection ERROR!!!");
                return;
            }
            try{
                Thread.sleep( 100 ); 
            }catch(InterruptedException e){}
        }
        // Сохроняем данные профиля пользователя
        Profile.Save(profile);

        AllUnitsID = new Unit[ MAX_UNIT ];

        mapInfo.loadMap();
        HUD = null;
        GUILoad(true);

        curCam = mainCam = new GameCamera( map.szp );

        refreshEffects.miniMap.Init();
        preEffects = new poolEffects();
        postEffects = new poolEffects();

        mainCam.Init();
        //xm = w >> 1; ym = h >> 1;

        infoWin.Init();
        gameTimer.Init();
        gameTimer.startTimer( new refreshEffects( gameCycle ) );
        //startTimer( new refreshTimer() );

        //for( int i=bullets.length-1; i>=0; i-- )
            //bullets[i].Init();

        CShost.startPlay();

        players = ClientServer.players;
        yourPlayerID = ClientServer.playerID;
        yourPlayer = ClientServer.yourPlayer;

        // Инициализация триггеров
        trigger.langReload();
        trigger.mapInit();

        // Переведение сервера-клиента в режим обработки игровой сцены
        initGame = true;

        // Устанавливаем интерфейс на внутри-игровой
        //HUD = inGame;
        
        // Ждём подключения всёх игроков
        //CShost.waitForAll();
        System.gc();
    }

    public static BitmapFont getDefaultFont(){
        return fonts.font;
    }

    //private static boolean fs[] = new boolean[40];
    //private static boolean fb[] = new boolean[40];

    public static BitmapFont getFont(int size, boolean bold){
        for (Fonts f : fonts.fonts) {
            if (f.fontSize==size && f.bold==bold) return f.font;
        }
        /*boolean b = bold ? fb[size] : fs[size];
        if (!b) {
            System.out.println("Font size = "+size+" ; bold = "+bold);
            if (bold) fb[size] = true; else fs[size] = true;
        }*/
        return fonts.font;
    }

    public static Unit createUnit(int x, int y, UnitData uData, Player owner) {
        return createUnit(x, y, uData, owner.owner);
    }

    public static Unit createUnit(int x, int y, UnitData uData, PlayerGroup owners) {
        if (numUnit<MAX_UNIT && uData!=null) {
            Unit u = uData.createUnit(owners);
            u.setPos(x, y);
            return addUnit(u);
        }
        return null;
    }

    public static Unit addUnit(Unit u) {
        if (u!=null && (u.ID<0 || AllUnitsID[u.ID]!=u)) {
            u.pred = allUnits.last;
            allUnits.add(u);
            u.killer = null;
            while (AllUnitsID[lastID]!=null) {
                lastID ++;
                if( lastID>=MAX_UNIT ) lastID = 0;
            }
            if( lastID>=MAX_UNIT ) lastID = 0;
            AllUnitsID[ lastID ] = u;
            u.ID = lastID;
            numUnit ++;
        }
        return u;
    }

    public static void backToLife(Unit u) {
        if( u!=null ){
            addUnit( u );
            //if (u.ID>=0) u.setPos(u.px, u.py);
            u.timeDie = - u.uData.time_die;
            u.status |= IS_ALIVE;
            if( u.uLvl!=null ){
                u.uLvl.setHP( u.uLvl.max_hp );
                u.uLvl.setMana( u.uLvl.max_mp );
            }
        }
    }

    public static Unit getUnitByID( int id ){
        return AllUnitsID[ id ];
    }

    public static void connect(boolean serv){
        if (mapInfo!=null) mapInfo.loadLobby();
        PlayersField.connect();
        if (portT==null) port = 25000;
        else port = Integer.parseInt(portT);

        if (serv) {
            CShost = new Server(port);
        } else {
            if (hostT==null) hostT = "localhost";
            try {
                host = InetAddress.getByName(hostT);
            } catch (UnknownHostException ex) {}
            CShost = new Client( host, port );
        }
        CShost.start();
    }

    public static void connectOffline(){
        if (mapInfo!=null) mapInfo.loadLobby();
        PlayersField.connect();
        if (portT==null) port = 25000;
        else port = Integer.parseInt(portT);

        CShost = new ClientServer();
        ClientServer.runOffline();
    }

    public static void mapClose(){
        if (trigger!=null) {
            trigger.dispose();
            trigger = null;
        }
        //mapInfo.dispose();
        //mapInfo = null;
        gameTimer.deleteAll();
        Action.deleteAll();
        initGame = false;
        visUnits = null;
        allUnits.deleteFast();
        if (map!=null) {
            map.dispose();
            map = null;
        }
        AllUnitsID = null;
        if (preEffects!=null) preEffects.deleteFast();
        if (postEffects!=null) postEffects.deleteFast();
        preEffects = postEffects = null;
        poolEffects.deletePool();
        if (uData!=null) {
            for (UnitData ud : uData) ud.dispose();
            uData = null;
        }
        //GUILoad(true);
        System.gc();
    }

    public static Unit getLastUnit() {
        return (Unit) Game.allUnits.last;
    }
 
    public static void disconnect(){
        if (mapInfo!=null) mapInfo.connect = false;
        Server.closeServ = true;
        mapClose();
        HUD = Menu;
        ClientServer.disconnect();
        for (Player p : ClientServer.players) p.disconnect();
    }

    public int getDirection(){
        int dir = 0;
        if( up || ym==0 ){
            if( right || xm==(w-1) ) dir = 2; else
            if( left || xm==0 ) dir = 8; else
            dir = 1;
        } else if( down || ym==(h-1) ){
            if( right || xm==(w-1) ) dir = 4; else
            if( left || xm==0 ) dir = 6; else
            dir = 5;
        } else
        if( right || xm==(w-1) ) dir = 3; else
        if( left || xm==0 ) dir = 7;
        return dir;
    }

    @Override
    public void render(){
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaT = Gdx.graphics.getDeltaTime();
        tFrame = (int) (deltaT * 1000f);
        if (Intro!=null) Intro.draw(g);
        else gameRender();
    }

    public void gameRender(){
        if (reset!=RESET_NOT) {
            switch (reset) {
                case RESET_LANGUAGE:
                    Settings.Save(settings);
                    settings.applyLanguage();
                    Menu.langReload();
                    if (inGame!=null) inGame.langReload();
                    if (trigger!=null) trigger.langReload();
                    break;
                case RESET_START_GAME:
                    Init();
                    break;
                case RESET_SETTINGS:
                    Game.settings.reset();
                    Settings.Save(Game.settings);
                    Game.settings.apply();
                    Game.GUILoad(true);
                    break;
                case RESET_LOAD:
                    Load();
            }
            reset = RESET_NOT;
        }
        HUD.onGUI(xm, ym, false);
        keyPressed = keyRelessed = 0;
        if (mWheel>0) mWheel --; else
        if (mWheel<0) mWheel ++;

        int oldCur = curCur;
        curCur = CURSOR_DEFAULT;
        if (initGame) {
            curCam.setDir(getDirection());
            mRect = curCam.getMousePos(xm, ym, xsd, ysd);

            gameTimer.incAllTimers();
            CShost.UpdateState();

            g.begin();
            g.setProjectionMatrix(curCam.combined);
            map.draw(g);
            render.preRender(g);
            refreshEffects.refreshAll(g);
        } else {
            g.begin();
        }
        g.setProjectionMatrix(hudCam.combined);
        HUD.draw(g);
        Interface.drawDragged(g, xm, ym);
        g.end();
        if (!HUD.pressed()) Interface.setDragged(null);
        //if (mbut1 || mbut2) statusKey = DO_NOTHING;

        refreshControl();

        if (oldCur!=curCur) {
            Pixmap pm = curImage[curCur];
            if (curCur>=NUM_TOP_LEFT_CURSORS) Gdx.input.setCursorImage(pm, pm.getWidth() >> 1, pm.getHeight() >> 1);
            else Gdx.input.setCursorImage(pm, 0, 0);
        }

        // Отрисовываем загрузочный экран
        g.reset();
        if (reset!=RESET_NOT) Loading.LoadScreenDraw();

        if (settings.showFPS) {
            g.begin();
            g.setFont(getDefaultFont());
            int FPS = Gdx.graphics.getFramesPerSecond();
            if (FPS>=50) g.setColor(Color.GREEN); else
            if (FPS>=30) g.setColor(Color.YELLOW);
            else g.setColor(Color.RED);
            g.drawString("FPS: "+FPS, 10, 10);
            g.end();
        }
    }

    public static void refreshControl(){
        if (initGame) {
            if (chooseMainUnit) {
                Unit u = yourPlayer.mainUnit;
                CShost.selectUnit(u);
                curCam.moveCam(u.px, u.py);
            }
        }
    }

    @Override
    public void create() {
        game = this;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        g = new Graphics();

        Gdx.input.setInputProcessor(this);

        settings = Settings.Load();
        Startup = Image.loadImage("startup.png");
        Intro = new intro();
        reset = RESET_LOAD;
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    public static void gameExit(){
        Game.game.dispose();
        Gdx.app.exit();
    }

    @Override
    public void dispose() {
        Menu.dispose();
        if (inGame!=null) inGame.dispose();
        mainMap.dispose();
        fonts.dispose();
        if (Intro!=null) Intro.dispose();
        disconnect();
        Image.disposeAll();
        TexturePacks.disposeAll();
        //for (Pixmap pm : curImage) pm.dispose();
    }

    @Override
    public boolean keyDown(int k) {
        if (stage!=null) stage.keyDown(k);
        if (k==Keys.ESCAPE) keyPressed = -1; else
        if (k==Keys.CONTROL_LEFT || k==Keys.CONTROL_RIGHT) control = true; else
        if (k==Keys.SHIFT_LEFT || k==Keys.SHIFT_RIGHT) shift = true; else
        if (k==Keys.ALT_LEFT || k==Keys.ALT_RIGHT) alt = true;
        else {
            if (Game.settings!=null) {
                int keys[] = Game.settings.keyCodes;
                if (keys[22]==k) {
                    ScreenshotFactory.saveScreenshot();
                    return true;
                }
                if (initGame) {
                    if (keys[1]==k) up = true; else
                    if (keys[2]==k) down = true; else
                    if (keys[3]==k) left = true; else
                    if (keys[4]==k) right = true; else
                    if (Game.HUD==inGame) {
                        if (keys[5]==k) chooseMainUnit = true; else
                        if (keys[6]==k) { statusKey = DO_ATTACK; return true; }
                    }
                }
            }
            keyPressed = k;
        }
        return true;
    }

    @Override
    public boolean keyUp(int k) {
        if (stage!=null) stage.keyUp(k);
        if (k==Keys.ESCAPE) keyPressed = -1; else
        if (k==Keys.CONTROL_LEFT || k==Keys.CONTROL_RIGHT) control = false; else
        if (k==Keys.SHIFT_LEFT || k==Keys.SHIFT_RIGHT) shift = false; else
        if (k==Keys.ALT_LEFT || k==Keys.ALT_RIGHT) alt = false;
        else {
            int keys[] = Game.settings.keyCodes;
            if (keys[22]==k) return true;
            if (initGame && Game.settings!=null) {
                if (keys[1]==k) up = false; else
                if (keys[2]==k) down = false; else
                if (keys[3]==k) left = false; else
                if (keys[4]==k) right = false; else
                if (Game.HUD==inGame) {
                    if (keys[5]==k) chooseMainUnit = false;
                }
            }
            keyRelessed = k;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        if (stage!=null) stage.keyTyped(character);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (stage!=null) stage.touchDown(screenX, screenY, pointer, button);
        if (button==Buttons.LEFT) mbut1 = true; else
        if (button==Buttons.RIGHT) mbut2 = true;
        xsd = xm = screenX; ysd = ym = screenY;
        m_drag = false;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (stage!=null) stage.touchUp(screenX, screenY, pointer, button);
        if (button==Buttons.LEFT) {
            mbut1 = false; but1Up = true;
        } else
        if (button==Buttons.RIGHT) {
            mbut2 = false; but2Up = true;
        }
        xm = screenX; ym = screenY;
        m_drag = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (stage!=null) stage.touchDragged(screenX, screenY, pointer);
        if (statusKey==DO_SELECT_UNITS) { xm = screenX; ym = screenY; }
        else { xsd = xm = screenX; ysd = ym = screenY; }
        m_drag = true;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (stage!=null) stage.mouseMoved(screenX, screenY);
        if (statusKey==DO_SELECT_UNITS) { xm = screenX; ym = screenY; }
        else { xsd = xm = screenX; ysd = ym = screenY; }
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        if (stage!=null) stage.scrolled(amount);
        if (mWheel==0) mWheel = amount * 12;
        else mWheel += amount;
        return true;
    }
}
