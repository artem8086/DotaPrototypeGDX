package art.soft.units;

import art.soft.Game;
import art.soft.utils.Image;
import art.soft.utils.Image;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Артем
 */
public class AnimData {

    private static final HashMap<String, AnimData> allAnims = new HashMap<String, AnimData>();
    
    public Image[] Data;
    public short[][][] Sdat;
    public short[] Time;
    public String anim;

    public static AnimData load( String dir ){
        if (allAnims.containsKey(dir)) {
            return allAnims.get(dir);
        }
        try{
            DataInputStream dis = new DataInputStream(Game.openFile( dir+"anim.pak" ).read());
            int aNum = dis.read();
            AnimData ad = new AnimData();
            ad.anim = dir;
            ad.Data = new Image[aNum];
            ad.Sdat = new short[aNum][][];
            ad.Time = new short[aNum];
            for( int i=0; i<aNum; i++ ){
                ad.Data[i] = Image.loadImage(dir+dis.readUTF()+".png");
                int num_dir = dis.read();
                ad.Sdat[i] = new short[num_dir][];
                int num_spr = dis.read();
                int size = num_spr << 3;
                ad.Time[i] = dis.readShort();
                for( int j=0; j<num_dir; j++ ){
                    short[] t = ad.Sdat[i][j] = new short[size];
                    for( int s=0; s<size; s++ )
                        t[s] = dis.readShort();
                }
            }
            dis.close();
            allAnims.put(dir, ad);
            return ad;
        }catch( IOException ie ){}
        return null;
    }

    public void dispose() {
        allAnims.remove(anim);
        for (int i=Data.length-1; i>=0; i--)
            Data[i].dispose();
    }
}
