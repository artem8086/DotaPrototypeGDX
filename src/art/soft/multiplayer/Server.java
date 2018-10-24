package art.soft.multiplayer;

import art.soft.*;
import static art.soft.Game.AllUnitsID;
import static art.soft.Game.MAX_UNIT;
import art.soft.units.Unit;
import static art.soft.units.Unit.IS_ALIVE;
import java.io.*;
import java.net.*;
/**
 *
 * @author Артем
 */
public class Server extends ClientServer {

public static final int MAX_UNIT_DATA_SIZE = 20;
public static final int UNIT_TYPE_MASK = 0x1FFF;
public static final int UNIT_IS_ALIVE = 0x2000;
public static final int UNIT_HAVE_MANA = 0x4000;
public static final int UNIT_HAVE_KILLER = 0x4000;
//public static final int UNIT_HAVE_POS = 0x8000;

private static byte[] messID;
public static int ofs;

public static boolean closeServ = false;

public Server( int port ){
    super();
    this.port = port;
    yourPlayer = players[0];
    yourPlayer.ServerPlayer( null, port );
    yourPlayer.setName( Game.profile.name );

    messID = new byte[ALL_PLAYER];

    data[0] = (byte)lastMessID;
    byte[] nick = Game.profile.name.getBytes();
    data[1] = PLAYER_SET_INFO;
    data[3] = (byte) nick.length;
    System.arraycopy( nick, 0, data, 4, nick.length );

    go = new Thread(this);
}

@Override
public void start(){
    go.start();
}

public Player playerConnect( InetAddress addr, int port ){
    int i=1;
    Player tp = null;
    for(; i<ALL_PLAYER; i++ ){
        tp = players[i];
        if (tp.player && (tp.inUse || players[i].lock)) tp = null; else break;
    }
    if( tp!=null && mess[1]==0 ){
        tp.ServerPlayer( addr, port );
        int len = mess[2] & 255;
        tp.setName( new String( mess, 3, len ) );

        System.arraycopy( mess, 2, data, 3+(i<<NAME_SHIFT_SIZE), len+1 );
        lastMessID ++;
        System.out.println("Player["+i+"] "+addr+":"+port+" connect!");
        return tp;
    }
    return null;
}

public Player getPlayerID( InetAddress addr, int port ){
    if (addr!=null)
    for( int i=ALL_PLAYER-1; i>0; i-- ){
        Player tp = players[i];
        if (tp.addr!=null){
            if( tp.addr.equals( addr ) && tp.port==port ){
                return tp;
            }
        }
    }
    return null;
}

@Override
public void run(){
    try{
        if (sock!=null) sock.close();
        sock = new DatagramSocket( port );
        sock.setSoTimeout( 100 );
        sock.setSendBufferSize(DATA_SIZE);
        sock.setReceiveBufferSize(MESSAGE_SIZE);
        if (ConnectError==CONECTION_DISCONNECT) { discon(); return; }
        ConnectError = CONECTION_OK;
    }catch(IOException e){
        if (ConnectError==CONECTION_DISCONNECT) { discon(); return; }
        ConnectError = CONECTION_ERROR;
        System.out.println("Cannot create server!");
    }
    if (ConnectError==CONECTION_OK) while( true ){
        if (ConnectError==CONECTION_DISCONNECT) { discon(); return; }
        try{
            pack.setData( mess );
            sock.receive( pack );
            InetAddress addr = pack.getAddress();
            int tPort = pack.getPort();
            pack.setData( data );
            Player p = getPlayerID( addr, tPort );
            if( p==null ){
                if( !start ){
                    p = playerConnect( addr, tPort );
                    if( p==null ) data[1] = DISCONNECT_PLAYER;
                } else {
                    data[1] = SERVER_PLAYING;
                }
            }
            if( p!=null ){
                if( !p.inUse ){
                    data[1] = DISCONNECT_PLAYER;
                    lastMessID ++;
                }
                boolean newMess = false;
                if( (byte) p.lastMessID!=mess[0] ){
                    p.lastMessID = mess[0];
                    newMess = true;
                    switch( mess[1] ){
                    case PLAYER_SET_INFO:
                        data[1] = PLAYER_SET_INFO;
                        data[2] = (byte) p.ID;
                        break;
                    case DISCONNECT_PLAYER:
                    }
                    p.setData( mess, 1 );
                }
                if( closeServ ){
                    data[1] = SERVER_CANCELED;
                    lastMessID ++;
                } else if( start ){

                    if (newMess || inControl) {
                        if (inControl) {
                            inControl = false;
                            data[2] ++;
                        }
                        lastMessID ++;
                        ofs = 2;
                        for( int i=0; i<ALL_PLAYER; i++ ){
                            Player tp = players[ i ];
                            if( tp.inUse ) {
                                if (p==tp) messID[i] ++;
                            }
                            data[ofs] = messID[i];
                            ofs ++;
                            ofs += setMessageData( data, ofs, tp );
                        }
                    }
                    int s = ofs + randomSave( data, ofs );

                    UnitSynhronised( p, data, s );
                }
            }
            data[0] = (byte) lastMessID;
            sock.send( pack );
        }catch(IOException e){}
    }
}

public void discon() {
    if (sock!=null) {
        if (sock.isConnected()) {
            lastMessID ++;
            data[0] = (byte) lastMessID;
            data[1] = (byte) DISCONNECT_PLAYER;
            boolean allDiscon = true;
            for (Player p : players) {
                if (p!=yourPlayer && p.player && p.inUse) allDiscon = false;
            }
            if (!allDiscon) do {
                try {
                    pack.setData( mess );
                    sock.receive( pack );
                    if (mess[1]==DISCONNECT_PLAYER) {
                        InetAddress addr = pack.getAddress();
                        int tPort = pack.getPort();
                        Player p = getPlayerID( addr, tPort );
                        if (p!=null) p.inUse = false;
                    }
                    pack.setData( data );
                    sock.send( pack );
                    allDiscon = true;
                    for (Player p : players) {
                        if (p!=yourPlayer && p.player && p.inUse) allDiscon = false;
                    }
                    if (allDiscon) break;
                } catch (IOException ex) {
                    break;
                }
            } while (true);
        }
        sock.close();
        sock = null;
    }
}

public void UnitSynhronised(Player p, byte[] data, int ofs){
    int o = ofs;
    ofs += 2;
    int len = data.length;
    int startID = p.lastUnitID;
    Unit u = null;
    while (len - ofs>MAX_UNIT_DATA_SIZE) {
        while( (u = AllUnitsID[ p.lastUnitID++ ])==null ){
            if( p.lastUnitID>=MAX_UNIT ) p.lastUnitID = 0;
        }
        if (p.lastUnitID>=MAX_UNIT) p.lastUnitID = 0;
        if (p.lastUnitID==startID || u==null) break;
        int t = u.uData.ID & UNIT_TYPE_MASK;
        t |= (u.status & IS_ALIVE)!=0 ? UNIT_IS_ALIVE | (u.uLvl.max_mp>0 ? UNIT_HAVE_MANA : 0): (u.killer!=null ? UNIT_HAVE_KILLER : 0);
        //t |= u instanceof UnitDynamic ? UNIT_HAVE_POS : 0;
        writeWord(data, ofs, t); ofs += 2;
        writeWord(data, ofs, u.ID); ofs += 2;
        if ((u.status & IS_ALIVE)!=0) {
            writeInt(data, ofs, Float.floatToIntBits(u.getHPper())); ofs += 4;
            if ((t & UNIT_HAVE_MANA)!=0) {
                writeInt(data, ofs, Float.floatToIntBits(u.getMPper())); ofs += 4;
            }
        } else {
            if ((t & UNIT_HAVE_KILLER)!=0) {
                writeWord(data, ofs, u.killer.ID);
                ofs += 2;
            }
        }
        //if ((t & UNIT_HAVE_POS)!=0) {
            writeInt(data, ofs, Float.floatToIntBits(u.x)); ofs += 4;
            writeInt(data, ofs, Float.floatToIntBits(u.y)); ofs += 4;
        //}
    }
    writeWord(data, o, ofs - 2 - o);
}

private int randomSave(byte[] data, int ofs){
    writeLong( data, ofs, random.getState(0) );
    ofs += 8;
    writeLong( data, ofs, random.getState(1) );
    return 16;
}

private static void writeLong( byte[] mess, int ofs, long l ){
    mess[ ofs ] = (byte) l;
    mess[ ofs+1 ] = (byte) (l >> 8);
    mess[ ofs+2 ] = (byte) (l >> 16);
    mess[ ofs+3 ] = (byte) (l >> 24);
    mess[ ofs+4 ] = (byte) (l >> 32);
    mess[ ofs+5 ] = (byte) (l >> 40);
    mess[ ofs+6 ] = (byte) (l >> 48);
    mess[ ofs+7 ] = (byte) (l >> 56);
}

private static void writeInt( byte[] mess, int ofs, int l ){
    mess[ ofs ] = (byte) l;
    mess[ ofs+1 ] = (byte) (l >> 8);
    mess[ ofs+2 ] = (byte) (l >> 16);
    mess[ ofs+3 ] = (byte) (l >> 24);
}

@Override
public void startPlay(){
    //yourPlayer = players[0];
    //yourPlayer.ServerPlayer( null, Game.port );
    PlayerGroup.yourPlayerBit = 1;
    playerID = 0;
    
    for( int i = 0; i<ALL_PLAYER; i++ )
        if (players[i].inUse) {
            players[i].Init();
        }
    clearPlayerInfo();
}

public void clearPlayerInfo(){
    data[1] = PLAYER_CONTROL;
    ofs = 2;
    for( int i = 0; i<ALL_PLAYER; i++ ){
        data[ ofs++ ] = 0;
        if( players[i].inUse ) data[ ofs ] = PLAYER_DO_NOTHING;
        else data[ ofs ] = DISCONNECT_PLAYER;
        ofs ++;
    }
    randomSave( data, ofs );
    lastMessID ++;
}

}
