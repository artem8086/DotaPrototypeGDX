package art.soft.multiplayer;

import art.soft.Game;
import art.soft.gui.Language;
import art.soft.units.*;
import java.net.*;
/**
 *
 * @author Артем
 */
public class ClientServer implements Runnable {

public final static int ALL_PLAYER = 32; // все игроки

public final static int PACK_SIZE = 5;

public static int NUM_STANDART_GROUP = 1;

// Общие сообщения сервера и клиента
public static final int PLAYER_SET_INFO = 0;
public static final int PLAYER_CONTROL = 1;
public static final int SERVER_PLAYING = -3;
public static final int SERVER_CANCELED = -2;
public static final int DISCONNECT_PLAYER = -1;

// Сообщения клиента о статусе его ввода (т.е. управления)
public static final int PLAYER_DO_NOTHING = 1;
public static final int PLAYER_MOVE_TO_POS = 2;
public static final int PLAYER_MOVE_TO_UNIT = 3;
public static final int PLAYER_SELECT_UNITS = 4;
public static final int PLAYER_ATTACK_UNIT = 5;
public static final int PLAYER_ATTACK_POS = 6;
public static final int PLAYER_LEARN_STATS = 7;
public static final int PLAYER_LEARN_ABILITY = 8;
public static final int PLAYER_CAST_ON_UNIT = 9;
public static final int PLAYER_AUTO_CAST_ON = 10;
public static final int PLAYER_AUTO_CAST_OFF = 11;
public static final int PLAYER_CAST_TO_POS = 12;
public static final int PLAYER_CAST_ALREADY = 13;
public static final int PLAYER_SELECT_UNIT_GRP = 14;
public static final int PLAYER_SELECT_UNIT = 15;
public static final int PLAYER_ADD_MOVE_TO_POS = 16;
public static final int PLAYER_ADD_MOVE_TO_UNIT = 17;
public static final int PLAYER_ADD_ATTACK_UNIT = 18;
public static final int PLAYER_ADD_ATTACK_POS = 19;
public static final int PLAYER_ADD_CAST_ON_UNIT = 20;
public static final int PLAYER_ADD_CAST_TO_POS = 21;
public static final int PLAYER_ADD_CAST_ALREADY = 22;
public static final int PLAYER_DROP_ITEM_TO_POS = 23;
public static final int PLAYER_DROP_ITEM_TO_UNIT = 24;
public static final int PLAYER_ADD_DROP_ITEM_TO_POS = 25;
public static final int PLAYER_ADD_DROP_ITEM_TO_UNIT = 26;
public static final int PLAYER_CHANGE_ITEM_SLOT = 27;
public static final int PLAYER_PING = 28;
public static final int PLAYER_PING_DANGER = 29;
public static final int PLAYER_USE_ITEM_ON_UNIT = 30;
public static final int PLAYER_USE_ITEM_TO_POS = 31;
public static final int PLAYER_USE_ITEM_ALREADY = 32;
public static final int PLAYER_ADD_USE_ITEM_ON_UNIT = 33;
public static final int PLAYER_ADD_USE_ITEM_TO_POS = 34;
public static final int PLAYER_ADD_USE_ITEM_ALREADY = 35;
public static final int PLAYER_BUY_ITEM = 36;

public static final int CONECTION_WAIT = 0;
public static final int CONECTION_OK = 1;
public static final int CONECTION_DISCONNECT = 2;
public static final int CONECTION_ERROR = -1;

public static int ConnectError;

public static final int NAME_SHIFT_SIZE = 5;

public static final int DATA_SIZE = 512;
public static final int MESSAGE_SIZE = 128;

public static PlayerGroup allPlayer, emptyGroup;

public static PlayerTeam[] teams;
public static Player[] players;
public static Player empty;
public static Thread go;
public static int lastMessID = 1000;
public static Player yourPlayer;
public static int playerID, port;
public static byte[] data, mess;
public static boolean start = false;
public DatagramSocket sock;
public static DatagramPacket pack;
public static RandomXS128 random = new RandomXS128(System.currentTimeMillis());
public static boolean inControl;

public ClientServer(){
    ConnectError = CONECTION_WAIT;
    for (int i=ALL_PLAYER-1; i>=0; i--) {
        players[i].reset();
    }
    Player.CSdata = this;
}

public void start(){}

public static void runOffline() {
    ConnectError = CONECTION_OK;
    yourPlayer = players[0];
    yourPlayer.ServerPlayer( null, 0 );
    yourPlayer.setName( Game.profile.name );
}

public static void Init(){
    data = new byte[ DATA_SIZE ];
    mess = new byte[ MESSAGE_SIZE ];
    
    players = new Player[ALL_PLAYER];

    for( int i=ALL_PLAYER-1; i>=0; i-- )
        players[i] = new Player( i );
    
    allPlayer = new PlayerGroup( -1, Language.words.allPlayers ); // группа содержит всех игроков
    emptyGroup = new PlayerGroup( 0, "" ); // пустая группа
    
    empty = new Player( -1 );
    
    pack = new DatagramPacket( data, data.length );
}

public static void disconnect(){
    ConnectError = CONECTION_DISCONNECT;
}

public void selectEnd(){
    if( yourPlayer.gUnit.slectEnd() ){
        yourPlayer.gUnit.setGroupToData( yourPlayer.uGrp, 0 );
        yourPlayer.inControl = PLAYER_SELECT_UNITS;
        inControl = true;
        lastMessID ++;
    }
}

public void setMovePos( int x, int y ){
    yourPlayer.unitAim = null;
    yourPlayer.xp = x;
    yourPlayer.yp = y;
    //yourPlayer.gUnit.setMovePos( x, y, playerID );
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = Game.shift ? PLAYER_ADD_MOVE_TO_POS : PLAYER_MOVE_TO_POS;
    inControl = true;
    lastMessID ++;
}

public void setMoveToUnit(Unit u){
    yourPlayer.unitAim = u;
    if( yourPlayer.FrendPlayer.groupInGroup( u.owners ) ){
        //yourPlayer.gUnit.setMoveToUnit( u, playerID );
        yourPlayer.inControl = Game.shift ? PLAYER_ADD_MOVE_TO_UNIT : PLAYER_MOVE_TO_UNIT;
        if (yourPlayer.isServer) yourPlayer.isDone = true;
        inControl = true;
        lastMessID ++;
    } else setAttackUnit( u );
}

public void setAttackUnit(Unit u){
    yourPlayer.unitAim = u;
    //if( !yourPlayer.FrendPlayer.groupInGroup( u.owners ) ){
        //yourPlayer.gUnit.setAttackUnit( u, playerID );
        yourPlayer.inControl = Game.shift ? PLAYER_ADD_ATTACK_UNIT : PLAYER_ATTACK_UNIT;
    //} else {
        //if( u.getHPper()<=u.attackHP )
            //yourPlayer.inControl = PLAYER_ATTACK_UNIT;
        //yourPlayer.gUnit.setMoveToUnit( u, playerID );
        //else yourPlayer.inControl = PLAYER_MOVE_TO_UNIT;
    //}
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    inControl = true;
    lastMessID ++;
}

public void setAttackPos( int x, int y ){
    yourPlayer.unitAim = null;
    yourPlayer.xp = x;
    yourPlayer.yp = y;
    //yourPlayer.gUnit.setAttackPos( x, y, playerID );
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = Game.shift ? PLAYER_ADD_ATTACK_POS : PLAYER_ATTACK_POS;
    inControl = true;
    lastMessID ++;
}

public void playerLearnStats(){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = PLAYER_LEARN_STATS;
    inControl = true;
    lastMessID ++;
}

public void playerLearnAbility(int skill){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = PLAYER_LEARN_ABILITY;
    yourPlayer.num = skill;
    inControl = true;
    lastMessID ++;
}

public void playerAutoCastON(int skill){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = PLAYER_AUTO_CAST_ON;
    yourPlayer.num = skill;
    inControl = true;
    lastMessID ++;
}

public void playerAutoCastOFF(int skill){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = PLAYER_AUTO_CAST_OFF;
    yourPlayer.num = skill;
    inControl = true;
    lastMessID ++;
}

public void castSpellOnUnit(int skill, Unit u){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = Game.shift ? PLAYER_ADD_CAST_ON_UNIT : PLAYER_CAST_ON_UNIT;
    yourPlayer.unitAim = u;
    yourPlayer.num = skill;
    inControl = true;
    lastMessID ++;
}

public void castSpellToPos(int skill, int x, int y){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = Game.shift ? PLAYER_ADD_CAST_TO_POS : PLAYER_CAST_TO_POS;
    yourPlayer.xp = x;
    yourPlayer.yp = y;
    yourPlayer.num = skill;
    inControl = true;
    lastMessID ++;
}

public void playerCastAlready(int skill){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = Game.shift ? PLAYER_ADD_CAST_ALREADY : PLAYER_CAST_ALREADY;
    yourPlayer.num = skill;
    inControl = true;
    lastMessID ++;
}


public void useItemOnUnit(int skill, Unit u){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = Game.shift ? PLAYER_ADD_USE_ITEM_ON_UNIT : PLAYER_USE_ITEM_ON_UNIT;
    yourPlayer.unitAim = u;
    yourPlayer.num = skill;
    inControl = true;
    lastMessID ++;
}

public void useItemlToPos(int skill, int x, int y){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = Game.shift ? PLAYER_ADD_USE_ITEM_TO_POS : PLAYER_USE_ITEM_TO_POS;
    yourPlayer.xp = x;
    yourPlayer.yp = y;
    yourPlayer.num = skill;
    inControl = true;
    lastMessID ++;
}

public void useItemAlready(int skill){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = Game.shift ? PLAYER_ADD_USE_ITEM_ALREADY : PLAYER_USE_ITEM_ALREADY;
    yourPlayer.num = skill;
    inControl = true;
    lastMessID ++;
}


public void dropItemToUnit(int item, Unit u){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = Game.shift ? PLAYER_ADD_DROP_ITEM_TO_UNIT : PLAYER_DROP_ITEM_TO_UNIT;
    yourPlayer.unitAim = u;
    yourPlayer.num = item;
    inControl = true;
    lastMessID ++;
}

public void playerBuyItem(int shop, int item){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = PLAYER_BUY_ITEM;
    yourPlayer.xp = shop;
    yourPlayer.yp = item;
    inControl = true;
    lastMessID ++;
}

public void dropItemToPos(int item, int x, int y){
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = Game.shift ? PLAYER_ADD_DROP_ITEM_TO_POS : PLAYER_DROP_ITEM_TO_POS;
    yourPlayer.xp = x;
    yourPlayer.yp = y;
    yourPlayer.num = item;
    inControl = true;
    lastMessID ++;
}

public void playerChangeItemSlot(int item1, int item2){
    if (item1!=item2) {
        if (yourPlayer.isServer) yourPlayer.isDone = true;
        yourPlayer.inControl = PLAYER_CHANGE_ITEM_SLOT;
        yourPlayer.xp = item1;
        yourPlayer.yp = item2;
        inControl = true;
        lastMessID ++;
    }
}

public void selectUnitFormGroup(int uNum) {
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = PLAYER_SELECT_UNIT_GRP;
    yourPlayer.num = uNum;
    inControl = true;
    lastMessID ++;
}

public void selectUnit(Unit u) {
    yourPlayer.unitAim = u;
    yourPlayer.inControl = PLAYER_SELECT_UNIT;
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    inControl = true;
    lastMessID ++;
}

public void playerPing(int x, int y, boolean danger) {
    yourPlayer.unitAim = null;
    yourPlayer.xp = x;
    yourPlayer.yp = y;
    //yourPlayer.gUnit.setMovePos( x, y, playerID );
    if (yourPlayer.isServer) yourPlayer.isDone = true;
    yourPlayer.inControl = danger ? PLAYER_PING_DANGER : PLAYER_PING;
    inControl = true;
    lastMessID ++;
}

public void UpdateState(){
    for( int i=ALL_PLAYER-1; i>=0; i-- ){
        if (players[i].player)
            players[i].UpdateState();
    }
}

public static void writeWord( byte[] mess, int ofs, int w ){
    mess[ ofs ] = (byte) w;
    mess[ ofs+1 ] = (byte) (w >> 8);
}

public int setMessageData( byte[] mess, int ofs, Player tp ){
    int size = 0;
    if( tp!=null ) {
        size = 1;
        mess[ ofs++ ] = (byte) tp.inControl;
        switch( tp.inControl ){
            case PLAYER_MOVE_TO_UNIT:
            case PLAYER_ATTACK_UNIT:
            case PLAYER_ADD_MOVE_TO_UNIT:
            case PLAYER_ADD_ATTACK_UNIT:
            case PLAYER_SELECT_UNIT:
                writeWord(mess, ofs, tp.unitAim.ID);
                size = 3;
                //tp.isDone = true;
                break;
            case PLAYER_MOVE_TO_POS:
            case PLAYER_ATTACK_POS:
            case PLAYER_ADD_MOVE_TO_POS:
            case PLAYER_ADD_ATTACK_POS:
            case PLAYER_CHANGE_ITEM_SLOT:
            case PLAYER_PING_DANGER:
            case PLAYER_BUY_ITEM:
            case PLAYER_PING:
                writeWord(mess, ofs, tp.xp); ofs += 2;
                writeWord(mess, ofs, tp.yp);
                size = 5;
                //tp.isDone = true;
                break;
            case PLAYER_SELECT_UNITS:
                int s = 1 + ((int) mess[ofs] << 1);
                System.arraycopy( tp.uGrp, 0, mess,  ofs, s );
                size += s;
                //tp.isDone = true;
                break;
            case PLAYER_LEARN_ABILITY:
            case PLAYER_AUTO_CAST_ON:
            case PLAYER_AUTO_CAST_OFF:
            case PLAYER_CAST_ALREADY:
            case PLAYER_ADD_CAST_ALREADY:
            case PLAYER_USE_ITEM_ALREADY:
            case PLAYER_ADD_USE_ITEM_ALREADY:
            case PLAYER_SELECT_UNIT_GRP:
                mess[ ofs ] = (byte) tp.num;
                size = 2;
                break;
            case PLAYER_CAST_ON_UNIT:
            case PLAYER_ADD_CAST_ON_UNIT:
            case PLAYER_USE_ITEM_ON_UNIT:
            case PLAYER_ADD_USE_ITEM_ON_UNIT:
            case PLAYER_ADD_DROP_ITEM_TO_UNIT:
            case PLAYER_DROP_ITEM_TO_UNIT:
                mess[ ofs ++ ] = (byte) tp.num;
                writeWord(mess, ofs, tp.unitAim.ID);
                size = 4;
                break;
            case PLAYER_CAST_TO_POS:
            case PLAYER_ADD_CAST_TO_POS:
            case PLAYER_USE_ITEM_TO_POS:
            case PLAYER_ADD_USE_ITEM_TO_POS:
            case PLAYER_ADD_DROP_ITEM_TO_POS:
            case PLAYER_DROP_ITEM_TO_POS:
                mess[ ofs++ ] = (byte) tp.num;
                writeWord(mess, ofs, tp.xp); ofs += 2;
                writeWord(mess, ofs, tp.yp);
                size = 6;
                break;
        }
    }
    /*if( tp==yourPlayer ){
        tp.inControl = PLAYER_DO_NOTHING;
        inControl = false;
    }*/
    return size;
}

public void startPlay() {
    PlayerGroup.yourPlayerBit = 1;
    playerID = 0;
    
    for( int i = 0; i<ALL_PLAYER; i++ )
        if (players[i].inUse) {
            players[i].Init();
        }
}

@Override
public void run(){}
}
