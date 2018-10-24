package art.soft.gui;

import art.soft.gui.buttons.IButton;
import art.soft.Game;
import static art.soft.Game.openFile;
import art.soft.Graphics;
import art.soft.gui.buttons.TButton;
import art.soft.utils.Rectangle;
import art.soft.gui.buttons.DragTarget;
import art.soft.gui.buttons.DraggedBut;
import art.soft.utils.TexturePacks;

/**
 *
 * @author Артем
 */
public class Interface {
    
    public static final int MOUSE_OVER = 0;
    public static final int BUTTON_PRESSED = 1;
    public static final int BUTTON_RELESSED = 2;
    public static final int ALTERNATIVE_PRESSED = 3;
    public static final int ALTERNATIVE_RELESSED = 4;
    public static final int MOUSE_WHEEL_ROTATE = 5;


    public static int trans_x, trans_y;
    public static Rectangle rect = new Rectangle();

    public static DraggedBut draggedBut;
    public static boolean dragged = false;
    public static boolean butClear;
    public int activeWin = Integer.MAX_VALUE;

    public TButton keyBut;

    public IButton buttons[];
    public int width, height;
    public IButton selBut; // выбранная кнопка
    public int mbx, mby; // координаты мыши на кнопке
    private double xk, yk; // коэфицеент масштабирования
    public float xt, yt; // смещение интерфейса/окна
    public int xm, ym; // координаты мыши в интерфейсе/окне
            // При появлении окна
    public int vxs, vys; // скорость передвижения окна по осям
    public int xas, yas; // координаты цели прибытия
    public int xss, yss; // начальные координаты окна
            // При скрытии окна
    public int vxe, vye; // скорость передвижения окна по осям
    public int xae, yae; // координаты цели прибытия
    public int xse, yse; // начальные координаты окна
    
    public boolean show = true; // обробатывать ли интерфейс
    public boolean close = false;
    public boolean delete = false; // удалять ли окно после скрытия

            // Текстурные атласы интерфейса
    public TexturePacks textures[];
    
    public Interface windows, lastWin, next, prev;
    public String externalWindow;
    public String nextWindow;

    public void copyState(Interface u){
        u.close = close;
        u.show = show;
        u.close = close;
        
        u.xm = xm; u.ym = ym;
        u.yt = yt; u.yt = yt;
        u.mbx = mbx; u.mby = mby;
        u.xse = xse; u.yse = yse;
        u.activeWin = activeWin;
        
        Interface w1 = windows;
        Interface w2 = u.windows;
        while (w1!=null && w2!=null) {
            w1.copyState(w2);
            w1 = w1.next;
            w2 = w2.next;
        }
    }


    public void load(){
        IButton.curWin = this;
	double xk = (double) Game.w / width;
        double yk = (double) Game.h / height;
        xt *= xk; yt *= yk;
        
        xss *= xk; yss *= yk;
        vxs *= xk; vys *= yk;
        xas *= xk; yas *= yk;

        vxe *= xk; vye *= yk;
        xae *= xk; yae *= yk;
        //openWin();

        if (textures!=null)
        for (TexturePacks tp : textures)
            tp.Load();

        if (keyBut!=null) keyBut.load(xk, yk);
        
        if (buttons!=null)
        for (IButton but : buttons)
            but.load(xk, yk);
        
        Interface win = windows;
        if (win==null && externalWindow!=null) {
            win = windows = Game.json.fromJson(Interface.class, openFile(externalWindow));
        }
        externalWindow = null;
        Interface p = null;
        while (win!=null) {
            win.load();
            lastWin = win;
            win.prev = p;
            p = win;
            win = win.next;
        }
        if (next==null && nextWindow!=null) {
            next = Game.json.fromJson(Interface.class, openFile(nextWindow));
        }
        nextWindow = null;
    }

    public boolean isShow() {
        return show && !close;
    }

    public void openWin(){
        show = true;
        close = false;
        if (vxs!=0 || vys!=0) {
            xt = xss; yt = yss;
        }
    }

    public void hideWin(){
        if (vxe!=0 || vye!=0) {
            xse = (int) xt; yse = (int) yt;
            close = true;
        } else {
            show = false;
        }
    }

    public boolean onGUI(int x, int y, boolean but){
        if (show) {
            x -= xt; y -= yt;
            xm = x; ym = y;
            Interface win = lastWin;
            while (win!=null) {
                if (win.onGUI(x, y, but)) but = true;
                win = win.prev;
            }
            IButton.curWin = this;
            selBut = null;
            if (buttons!=null) {
                for (IButton b : buttons) {
                    if (b.onKey()!=null) but = true;
                }
                if (!but) for (int i = buttons.length - 1; i>=0; i--) {
                    if ((selBut = buttons[i].onGUI(x, y))!=null) return true;
                }
            }
        }
        return but;
    }

    public void draw(Graphics g){
        if (show) {
            moveWin();
            trans_x += xt;
            trans_y += yt;
            g.translate(xt, yt);
            IButton.curWin = this;
            if (buttons!=null)
            for (IButton but : buttons)
                but.draw(g);

            Interface win = windows;
            while (win!=null) {
                /*if (!win.show && win.delete) {
                    win = win.next;
                    deleteWindow(win);
                } else {*/
                    win.draw(g);
                    win = win.next;
                //}
            }
            g.translate(-xt, -yt);
            trans_x -= xt;
            trans_y -= yt;
        }
    }

    public static void drawDragged(Graphics g, int x, int y) {
        if (dragged && draggedBut!=null) {
            g.reset();
            draggedBut.DraggedDraw(g, x, y);
        }
    }

    public static void setDragged(DragTarget dt){
        if (Game.m_drag) {
            if (dt!=null) dt.draggedOver(draggedBut);
        } else {
            if (dt!=null) dt.DropIn(draggedBut);
            draggedBut = null;
            Game.but1Up = Game.but2Up = dragged = false;
        }
    }

    public boolean pressed(){
        if (show) {
            Interface win = lastWin;
            while (win!=null) {
                if (win.pressed()) return true;
                win = win.prev;
            }
            if (selBut!=null) {
                IButton.curWin = this;
                mbx = xm - selBut.x;
                mby = ym - selBut.y;
                if (draggedBut!=null) {
                    if (selBut!=draggedBut) dragged = true;
                }
                if (dragged) {
                    DragTarget dt = null;
                    if (selBut instanceof DragTarget) {
                        dt = (DragTarget) selBut;
                    }
                    setDragged(dt);
                } else {
                    //key = Game.keyPressed;
                    int code = MOUSE_OVER;
                    int key = 0;
                    if (Game.mbut1) {
                        if (selBut instanceof DraggedBut) {
                            draggedBut = (DraggedBut) selBut;
                        } else code = BUTTON_PRESSED;
                    } else
                    if (Game.mbut2) code = ALTERNATIVE_PRESSED; else
                    if (Game.but2Up) code = ALTERNATIVE_RELESSED; else
                    if (Game.but1Up) code = BUTTON_RELESSED; else
                    if (Game.mWheel!=0) {
                        code = MOUSE_WHEEL_ROTATE;
                        key = Game.mWheel;
                    }
                    butClear = true;
                    selBut.pressed(code, key);
                    Game.but1Up = Game.but2Up = false;
                    if (butClear) {
                        Game.mbut1 = Game.mbut2 = false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void setRect(int x1, int y1, int x2, int y2){
        if (x1>x2) { rect.x = x2; rect.width = x1 - x2; }
        else { rect.x = x1; rect.width = x2 - x1; }
        if (y1>y2) { rect.y = y2; rect.height = y1 - y2; }
        else { rect.y = y1; rect.height = y2 - y1; }
        if (rect.width==0) rect.width = 1;
        if (rect.height==0) rect.height = 1;
    }

    public void moveWin(){
        if (close) {
            if (vxe!=0 || vye!=0) {
                if (xae!=xt || yae!=yt) {
                    xt += vxe * Game.deltaT;
                    yt += vye * Game.deltaT;
                    setRect(xse, yse, xae, yae);
                    if (!rect.contains((int) xt, (int) yt)) {
                        xt = xae; yt = yae;
                        close = show = false;
                    }
                } else {
                    close = show = false;
                }
            }
        } else {
            if (vxs!=0 || vys!=0) {
                if (xas!=xt || yas!=yt) {
                    xt += vxs * Game.deltaT;
                    yt += vys * Game.deltaT;
                    setRect(xas, yas, xss, yss);
                    if (!rect.contains((int) xt, (int) yt)) {
                        xt = xas; yt = yas;
                    }
                }
            }
        }
    }

    public void setXY(int x, int y){
        xt = (int) (x * xk); yt = (int) (y * yk);
    }

/*    public void insertWindow( Interface win ){
        if (win!=null) {
            if( windows==null ){
                windows = lastWin = win;
                win.prev = null;
            } else {
                win.prev = lastWin;
                lastWin.next = win;
                lastWin = win;
            }
        }
    }

    public void deleteWindow( Interface win ){
        if (win!=null) {
            if (win.prev==null) windows = win.next;
            else win.prev.next = win.next;
            if (win==lastWin) lastWin = prev;
            else win.next.prev = win.prev;
            win.prev = win.next = null;
        }
    }
*/
    public Interface getWindow(int n){
        Interface w = windows;
        while (w!=null && n>0) {
            n --;
            w = w.next;
        }
        return w;
    }

    public static Interface getWindow(int[] address){
        if (address!=null) {
            Interface w = Game.HUD;
            for (int r : address) {
                w = r<0 ? IButton.curWin : w.getWindow(r);
            }
            return w;
        }
        return null;
    }

    public void langReload(){
        if (keyBut!=null) keyBut.langReload();
        if (buttons!=null)
        for (IButton ib : buttons) ib.langReload();
        Interface win = lastWin;
        while (win!=null) {
            win.langReload();
            win = win.prev;
        }
    }

    public void dispose(){
        Game.stage.clear();
        if (keyBut!=null) keyBut.dispose();
        if (buttons!=null)
        for (IButton ib : buttons) ib.dispose();
        if (textures!=null)
        for (TexturePacks tp : textures)
            tp.dispose();

        Interface win = lastWin;
        while (win!=null) {
            win.dispose();
            win = win.prev;
        }
    }
}
