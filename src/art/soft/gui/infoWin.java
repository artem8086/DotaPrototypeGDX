package art.soft.gui;

import art.soft.Game;
import art.soft.Graphics;
import art.soft.utils.Image;
import art.soft.units.*;
import com.badlogic.gdx.graphics.Color;
import art.soft.utils.Image;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
/**
 *
 * @author Артем
 */
public class infoWin {

    public static BitmapFont HPf;
    public static Image winB;

    public static void Init(){
        HPf = Game.getFont(13, true);
        winB = Image.loadImage("gui/broadcaster_i11.png");
    }
    
    public static int drawCreepHP( Graphics g, int xp, int yp, int d, Unit u, boolean isFrend ){
        g.setColor( Color.BLACK );
        yp -= 7;
        d += 22;
        g.fillRect( xp-(d>>1), yp, d, 7 );
        if( isFrend ){
            g.setColor( Color.GREEN );
        } else {
            g.setColor( Color.RED );
        }
        d -= 2;
        float t = (float) d / u.uLvl.max_hp;
        int hpw = (int) (u.uLvl.getCurHP() * t);
        g.fillRect( xp-(d>>1), yp+1, hpw, 5 );
        return 15;
    }
    
    public static int drawHeroHP( Graphics g, int xp, int yp, int d, Unit u, boolean isFrend ){
        int h = 8;
        yp -= h; d += 48;
        int x = xp-(d>>1);
        g.setFont( HPf );
        g.setStroke(1);
        g.setColor( Color.BLACK );
        g.fillRect( xp-(d>>1), yp, d, 8 );
        d -= 2; x ++;
        int Uhp = u.uLvl.getCurHP();
        int max = u.uLvl.max_hp;
        float t = (float) d / max;
        int hpw = (int) (Uhp * t);
        int hpo = (int) (u.uLvl.getOldHP() * t);
        hpo -= hpw;
        if (hpo>0) {
            g.setColor( Color.WHITE );
            g.fillRect( x+hpw, yp+1, hpo, 6 );
        }
        if( isFrend ){
            g.setColor( Color.GREEN );
        } else {
            g.setColor( Color.RED );
        }
        g.fillRect( x, yp+1, hpw, 6 );
        
        g.setColor( Color.BLACK );
        int ihp = 250 * ((max / 10000)+1);
        int n = Uhp / ihp;
        float ph = 0;  t *= ihp;
        for( int i=0; i<n; i++ ){
            ph += t;
            if( (i & 3)==3 ) g.fillRect( x-1+(int) ph, yp, 3, 7 );
            else g.fillRect( x+(int) ph, yp, 1, 7 );
        }
        if( isFrend && u.uLvl.max_mp>0 ){
            h += 5;
            g.fillRect( x-1, yp+7, d+2, 6 );
            g.setColor( Color.BLUE );
            t = (float) d / u.uLvl.max_mp;
            hpw = (int) (u.uLvl.getCurMana() * t);
            g.fillRect( x, yp+8, hpw, 4 );
        }
        if (u==Game.yourPlayer.mainUnit) {
            String hp = ""+Uhp;
            int fh = g.fontHeight()+1;
            yp -= fh; h += fh-6;
            g.setColor( Color.BLACK );
            hpw = g.stringWidth( hp ) >> 1;
            g.fillRect( xp-hpw-5, yp+g.fontAscent(), hpw+hpw+10, fh+g.fontDescent() );
            max >>>= 1;
            if( Uhp<max ){
                max >>>= 1;
                if( Uhp<max ) g.setColor( Color.RED );
                else g.setColor( Color.YELLOW );
            } else g.setColor( Color.GREEN );
            g.drawString( hp, xp-hpw, yp+2+g.fontAscent());
        }
        return h;
    }
    
    public static void drawWindow(Graphics gr, int xp, int yp, String[] s, int n){
        gr.setFont(Game.getDefaultFont());
        int ft = gr.fontHeight();
        int h = n * ft + ft;
        int[] sw = new int[n];
        int w = 0;
	for( int i=n-1; i>=0; i-- ){
            int t = gr.stringWidth( s[i] );
            sw[i] = t >> 1;
            if( t>w ) w = t;
        }
        w += 24;
        int x = xp - (w>>1);
        int y = yp - h;
        gr.setAlpha(0.6f);
	gr.draw(winB, x, y, w, h);
        gr.setAlphaNormal();

        gr.setColor(Color.WHITE);
        yp -= (ft >> 1) - gr.fontAscent();
        for( int i=n-1; i>=0; i-- ){
            yp -= ft;
            gr.drawString( s[i], xp-sw[i], yp );
        }
    }

    public void dispose() {
        if (winB!=null) { winB.dispose(); winB = null; }
    }
}
