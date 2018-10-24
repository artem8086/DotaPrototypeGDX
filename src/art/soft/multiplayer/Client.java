package art.soft.multiplayer;

import java.io.*;
import java.net.*;
import art.soft.*;
import static art.soft.multiplayer.Player.readWord;
import static art.soft.multiplayer.Server.UNIT_HAVE_KILLER;
import static art.soft.multiplayer.Server.UNIT_HAVE_MANA;
import static art.soft.multiplayer.Server.UNIT_IS_ALIVE;
import static art.soft.multiplayer.Server.UNIT_TYPE_MASK;
import art.soft.units.Unit;
import static art.soft.units.Unit.IS_ALIVE;
/**
 *
 * @author Артем
 */
public class Client extends ClientServer{

public static int servCode = 0;
public static int lastServID;

private static InetAddress host;
private static final int UNITS_BUFF_SIZE = 65536;
private static int lastBuffAddr;
private static byte[] uBuff1, uBuff2;

public Client(InetAddress host, int port){
    super();
    this.host = host;
    this.port = port;
    uBuff1 = new byte[ UNITS_BUFF_SIZE ];
    uBuff2 = new byte[ UNITS_BUFF_SIZE ];

    go = new Thread(this);
}

@Override
public void start(){
    go.start();
}

@Override
public void run(){
    if( host==null ) {
        if (ConnectError==CONECTION_DISCONNECT) { discon(); return; }
        ConnectError = CONECTION_ERROR;
        System.out.println("Host addres ERROR!!!");
    } else try{
        if (ConnectError==CONECTION_DISCONNECT) { discon(); return; }
        ConnectError = CONECTION_WAIT;
        if( sock!=null ) sock.close();
        sock = new DatagramSocket();
        sock.connect( host, port );
        sock.setSoTimeout( 100 );
        sock.setSendBufferSize(MESSAGE_SIZE);
        sock.setReceiveBufferSize(DATA_SIZE);
        lastMessID ++;
        mess[0] = (byte) lastMessID;
        lastServID = 1000;
        byte[] nick = Game.profile.name.getBytes();
        mess[1] = PLAYER_SET_INFO;
        mess[2] = (byte) nick.length;
        System.arraycopy( nick, 0, mess, 3, nick.length );
        if (ConnectError==CONECTION_DISCONNECT) { discon(); return; }
        ConnectError = CONECTION_OK;
    }catch(IOException e){
        if (ConnectError==CONECTION_DISCONNECT) { discon(); return; }
        ConnectError = CONECTION_ERROR;
        System.out.println("Server not found!");
    }
    if (ConnectError==CONECTION_OK) {
        while( !start ){
            if (ConnectError==CONECTION_DISCONNECT) { discon(); return; }
            try{
                mess[0] = (byte) lastMessID;
                pack.setData( mess );
                sock.send( pack );
                pack.setData( data );
                sock.receive( pack );
            }catch(IOException e){}
            if( lastServID!=data[0] ){
                lastServID = data[0];

                servCode = data[1];
                if( servCode<0 ){
                    disconnect();
                } else if( servCode==PLAYER_SET_INFO ){
                    playerID = data[2];
                    for( int i=ALL_PLAYER-1; i>=0; i-- ){
                        if (players[i].player) {
                            int a = 3+(i<<NAME_SHIFT_SIZE);
                            int len = data[ a ];
                            if( len==0 ) players[i].setName( null );
                            else{
                                players[i].setName( new String( data, a+1, len ) );
                                players[i].inUse = true;
                            }
                        }
                    }
                    //playerDC = true;
                }
            }
        }
        while( start ){
            if (ConnectError==CONECTION_DISCONNECT) { discon(); return; }
            try {
                if( (byte) lastMessID!=mess[0] ){
                    mess[ 0 ] = (byte) lastMessID;
                    setMessageData( mess, 1, yourPlayer );
                }
                pack.setData( mess );
                sock.send( pack );
                pack.setData( data );
                sock.receive( pack );
            }catch( IOException ex ){}
            switch( data[1] ){
                case PLAYER_CONTROL:
                    if( (byte) lastServID!=data[0] ){
                        lastServID = data[0];
                        int ofs = 2;
                        for( int i=0; i<ALL_PLAYER; i++ ){
                            Player tp = players[ i ];
                            if (tp.player) {
                                if( (byte) tp.lastMessID!=data[ ofs ] ){
                                    tp.lastMessID = data[ ofs++ ];
                                    ofs += tp.setData( data, ofs );
                                } else {
                                    ofs ++;
                                    ofs += empty.setData( data, ofs );
                                }
                            }
                        }
                        ofs += randomLoad( data, ofs );
                        UnitSynhronised( data, ofs );
                    }
                    break;
                /*case UNITS_POSITION:
                    //inControl = true;
                    break;*/
                case SERVER_CANCELED:
                    System.out.println( "Server canceld!" );
                    break;
                case DISCONNECT_PLAYER:
                    yourPlayer.disconnect();
                    disconnect();
            }
        }
    }
}

public void discon() {
    if (sock!=null) {
        if (sock.isConnected()) {
            lastMessID ++;
            mess[0] = (byte) lastMessID;
            mess[1] = (byte) DISCONNECT_PLAYER;
            do {
                try {    
                    pack.setData( mess );
                    sock.send( pack );
                    pack.setData( data );
                    sock.receive( pack );
                    if (data[1]==DISCONNECT_PLAYER) break; 
                } catch (IOException ex) {
                    break;
                }
            } while (true);
        }
        sock.close();
        sock = null;
    }
}

public void UnitSynhronised(byte[] data, int ofs){
    int s = readWord(data, ofs);
    if (s!=0) {
        ofs += 2;
        System.arraycopy(data, ofs, uBuff1, lastBuffAddr, s);
        lastBuffAddr += s;
    }
}

private int randomLoad(byte[] data, int ofs){
    random.setState( readLong( data, ofs ), readLong( data, ofs+8 ) );
    return 16;
}

public static int readInt(byte[] mess, int ofs){
    return (mess[ofs] & 255) | ((mess[ofs+1] & 255)<< 8) | ((mess[ofs+2] & 255)<<16) | ((mess[ofs+3] & 255)<<24);
}

public static long readLong(byte[] mess, int ofs){
    return (mess[ofs] & 255) | ((mess[ofs+1] & 255)<< 8) | ((mess[ofs+2] & 255)<<16) | ((mess[ofs+3] & 255)<<24) |
   ((mess[ofs+4] & 255)<<32) | ((mess[ofs+5] & 255)<<40) | ((mess[ofs+6] & 255)<<48) | ((mess[ofs+7] & 255)<<56);
}

@Override
public void startPlay(){
    yourPlayer = players[ playerID ];
    PlayerGroup.yourPlayerBit = 1 << playerID;
    for( int i = 0; i<ALL_PLAYER; i++ )
        if (players[i].inUse) {
            players[i].Init();
        }
    mess[1] = PLAYER_DO_NOTHING;
    lastMessID ++;
}

@Override
public void UpdateState(){
    super.UpdateState();
    if (lastBuffAddr!=0) {
        int size = lastBuffAddr;
        System.arraycopy(uBuff1, 0, uBuff2, 0, size);
        lastBuffAddr -= size;
        int ofs = 0;
        while (ofs<size) {
            int t = readWord(uBuff2, ofs); ofs += 2;
            int id = readWord(uBuff2, ofs); ofs += 2;
            Unit u = Game.getUnitByID(id);
            if (u==null) {
                System.out.println("ERROR! Unit with ID="+id+" dosn't exist!");
                break;
            } else {
                id = u.uData.ID;
                if (id != (t & UNIT_TYPE_MASK)) {
                    System.out.println("ERROR! Unit TYPE="+id+" not equals!");
                    break;
                } else {
                    if ((t & UNIT_IS_ALIVE)!=0) {
                        u.setHPper(Float.intBitsToFloat(readInt(uBuff2, ofs)));
                        ofs += 4;
                        if ((t & UNIT_HAVE_MANA)!=0) {
                            u.setMPper(Float.intBitsToFloat(readInt(uBuff2, ofs)));
                            ofs += 4;
                        }
                    } else {
                        if ((t & UNIT_HAVE_KILLER)!=0) {
                            id = readWord(uBuff2, ofs);
                            ofs += 2;
                            if ((u.status & IS_ALIVE)!=0) u.unitDieFrom(Game.getUnitByID(id));
                            else if (u.killer==null || u.killer.ID!=id)
                                System.out.println("ERROR!Unit killer ID["+id+"] != real killer!");
                                break;
                        } else {
                            if ((u.status & IS_ALIVE)!=0) u.unitDieFrom(null);
                            else if (u.killer!=null)
                                System.out.println("ERROR!Unit killer != real killer!");
                                break;
                        }
                    }
                    //if ((t & UNIT_HAVE_POS)!=0) {
                        u.setXYReal(Float.intBitsToFloat(readInt(uBuff2, ofs)),
                                Float.intBitsToFloat(readInt(uBuff2, ofs+4)));
                        ofs += 8;
                    //}
                }
            }
        }
    }
}

}
