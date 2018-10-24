package art.soft.multiplayer;

import art.soft.Game;
import art.soft.items.Shop;
import static art.soft.map.mapEffect.EFFECT_DANGER;
import static art.soft.map.mapEffect.EFFECT_PING;
import static art.soft.multiplayer.ClientServer.DISCONNECT_PLAYER;
import static art.soft.multiplayer.ClientServer.NAME_SHIFT_SIZE;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_ATTACK_POS;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_ATTACK_UNIT;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_CAST_ALREADY;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_CAST_ON_UNIT;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_CAST_TO_POS;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_DROP_ITEM_TO_POS;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_DROP_ITEM_TO_UNIT;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_MOVE_TO_POS;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_MOVE_TO_UNIT;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_USE_ITEM_ALREADY;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_USE_ITEM_ON_UNIT;
import static art.soft.multiplayer.ClientServer.PLAYER_ADD_USE_ITEM_TO_POS;
import static art.soft.multiplayer.ClientServer.PLAYER_ATTACK_POS;
import static art.soft.multiplayer.ClientServer.PLAYER_ATTACK_UNIT;
import static art.soft.multiplayer.ClientServer.PLAYER_AUTO_CAST_OFF;
import static art.soft.multiplayer.ClientServer.PLAYER_AUTO_CAST_ON;
import static art.soft.multiplayer.ClientServer.PLAYER_BUY_ITEM;
import static art.soft.multiplayer.ClientServer.PLAYER_CAST_ALREADY;
import static art.soft.multiplayer.ClientServer.PLAYER_CAST_ON_UNIT;
import static art.soft.multiplayer.ClientServer.PLAYER_CAST_TO_POS;
import static art.soft.multiplayer.ClientServer.PLAYER_CHANGE_ITEM_SLOT;
import static art.soft.multiplayer.ClientServer.PLAYER_DO_NOTHING;
import static art.soft.multiplayer.ClientServer.PLAYER_DROP_ITEM_TO_POS;
import static art.soft.multiplayer.ClientServer.PLAYER_DROP_ITEM_TO_UNIT;
import static art.soft.multiplayer.ClientServer.PLAYER_LEARN_ABILITY;
import static art.soft.multiplayer.ClientServer.PLAYER_LEARN_STATS;
import static art.soft.multiplayer.ClientServer.PLAYER_MOVE_TO_POS;
import static art.soft.multiplayer.ClientServer.PLAYER_MOVE_TO_UNIT;
import static art.soft.multiplayer.ClientServer.PLAYER_PING;
import static art.soft.multiplayer.ClientServer.PLAYER_PING_DANGER;
import static art.soft.multiplayer.ClientServer.PLAYER_SELECT_UNIT;
import static art.soft.multiplayer.ClientServer.PLAYER_SELECT_UNITS;
import static art.soft.multiplayer.ClientServer.PLAYER_SELECT_UNIT_GRP;
import static art.soft.multiplayer.ClientServer.PLAYER_USE_ITEM_ALREADY;
import static art.soft.multiplayer.ClientServer.PLAYER_USE_ITEM_ON_UNIT;
import static art.soft.multiplayer.ClientServer.PLAYER_USE_ITEM_TO_POS;
import static art.soft.multiplayer.ClientServer.data;
import static art.soft.multiplayer.ClientServer.playerID;
import java.net.*;
import art.soft.units.*;
/**
 *
 * @author Артем
 */
public class Player {

    public static ClientServer CSdata;
    public UnitGroupSelect gUnit;
    public Unit mainUnit;
    public String name;
    public boolean inUse, lock, player, isServer;
    public InetAddress addr;
    public int ID, port, lastUnitID;
    public int lastMessID, messNum;

    public PlayerGroup FrendPlayer;     // дружественные к вам игроки
    public PlayerGroup VisiblePlayers;  // игроки юнитов которых вы всегда видите
    public PlayerGroup owner;           // группа данного игрока

    public int startX, startY, color, goldResNum;
    public int resorces[];
    public int gold, gold_n; // ненадёжное и надёжное золото

    public int curShop, lastPrior, shopsMask;

    public boolean canAutoAttack;

    public PlayerTeam team;
    public int teamInx;

    //Текущее состояние ввода игрока
    public int inControl = ClientServer.PLAYER_DO_NOTHING;
    public boolean isDone = false;

    public byte[] uGrp;
    public UnitGroup selUnit;
    public int xp, yp, num;
    public Unit unitAim;

    public Player next; // для временного контейнера

    public Player( int id ){
        ID = id;
        gUnit = new UnitGroupSelect();
        uGrp = new byte[1+(UnitGroup.MAX_UNIT_IN_GROUP<<1)];
        owner = new PlayerGroup( 1 << ID, null );
        FrendPlayer = new PlayerGroup( 0, null );
        VisiblePlayers = new PlayerGroup( 0, null );
    }

    public void ServerPlayer( InetAddress addr, int port ){
        lock = false;
        inUse = true;
        this.addr = addr;
        this.port = port;
        isServer = true;
    }

    public int getGold() {
        return gold + gold_n;
    }

    public void decGold(int n) {
        if (gold>=n) gold -= n;
        else {
            gold_n -= n - gold;
            if (gold_n<0) gold_n = 0;
            gold = 0;
        }
    }

    public void refreshTrigger() {
        curShop = shopsMask = lastPrior = 0;
    }

    public void setName(String name) {
        owner.name = name;
        this.name = name;
    }

    public void kick_off() {
        System.out.println("Kick-off player: "+name);
        lock = inUse = false; //mainUnit = null;
        name = null;
        owner.name = null;
        if (!Server.start) data[ 3+(ID<<NAME_SHIFT_SIZE) ] = 0;
    }

    public static int readWord(byte[] mess, int ofs) {
        return (mess[ofs] & 255) | ((mess[ofs+1] & 255)<<8);
    }

    public int setData(byte[] mess, int ofs) {
        //int size = 0;
        //if( inUse ){
            int size = 1;
            inControl = mess[ ofs++ ];
            switch( inControl ){
                case PLAYER_MOVE_TO_POS:
                case PLAYER_ATTACK_POS:
                case PLAYER_ADD_MOVE_TO_POS:
                case PLAYER_ADD_ATTACK_POS:
                case PLAYER_CHANGE_ITEM_SLOT:
                case PLAYER_PING_DANGER:
                case PLAYER_BUY_ITEM:
                case PLAYER_PING:
                    xp = readWord(mess, ofs); ofs += 2;
                    yp = readWord(mess, ofs);
                    size = 5;
                    isDone = true;
                    break;
                case PLAYER_MOVE_TO_UNIT:
                case PLAYER_ATTACK_UNIT:
                case PLAYER_ADD_MOVE_TO_UNIT:
                case PLAYER_ADD_ATTACK_UNIT:
                case PLAYER_SELECT_UNIT:
                    unitAim = Game.getUnitByID( readWord(mess, ofs) );
                    size = 3;
                    isDone = true;
                    break;
                case PLAYER_SELECT_UNITS:
                    int s = 1 + ((int) uGrp[0] << 1);
                    System.arraycopy( mess, ofs, uGrp, 0, s );
                    size += s;
                    isDone = true;
                    break;
                case PLAYER_LEARN_STATS:
                    isDone = true;
                    break;
                case PLAYER_LEARN_ABILITY:
                case PLAYER_AUTO_CAST_ON:
                case PLAYER_AUTO_CAST_OFF:
                case PLAYER_CAST_ALREADY:
                case PLAYER_ADD_CAST_ALREADY:
                case PLAYER_USE_ITEM_ALREADY:
                case PLAYER_ADD_USE_ITEM_ALREADY:
                case PLAYER_SELECT_UNIT_GRP:
                    num = mess[ofs];
                    size = 2;
                    isDone = true;
                    break;
                case PLAYER_CAST_ON_UNIT:
                case PLAYER_ADD_CAST_ON_UNIT:
                case PLAYER_USE_ITEM_ON_UNIT:
                case PLAYER_ADD_USE_ITEM_ON_UNIT:
                case PLAYER_ADD_DROP_ITEM_TO_UNIT:
                case PLAYER_DROP_ITEM_TO_UNIT:
                    num = mess[ofs ++];
                    unitAim = Game.getUnitByID( readWord(mess, ofs) );
                    size = 4;
                    isDone = true;
                    break;
                case PLAYER_CAST_TO_POS:
                case PLAYER_ADD_CAST_TO_POS:
                case PLAYER_USE_ITEM_TO_POS:
                case PLAYER_ADD_USE_ITEM_TO_POS:
                case PLAYER_ADD_DROP_ITEM_TO_POS:
                case PLAYER_DROP_ITEM_TO_POS:
                    num = mess[ofs ++];
                    xp = readWord(mess, ofs); ofs += 2;
                    yp = readWord(mess, ofs);
                    size = 6;
                    isDone = true;
                    break;
                case DISCONNECT_PLAYER:
                    if (ID>=0) disconnect();
            }
        //}
        return size;
    }

    public void UpdateState() {
        if (isDone) {
            switch(inControl) {
                case PLAYER_DO_NOTHING: break;
                case PLAYER_MOVE_TO_UNIT:
                    gUnit.setMoveToUnit(unitAim, this, false );
                    break;
                case PLAYER_MOVE_TO_POS:
                    gUnit.setMovePos( xp, yp, this, false );
                    break;
                case PLAYER_ADD_MOVE_TO_UNIT:
                    gUnit.setMoveToUnit(unitAim, this, true );
                    break;
                case PLAYER_ADD_MOVE_TO_POS:
                    gUnit.setMovePos( xp, yp, this, true );
                    break;
                case PLAYER_SELECT_UNITS:
                    if( ID!=playerID )
                    gUnit.getGroupFromData( uGrp, 0 );
                    gUnit.selectUnitFormGroup( 0 );
                    break;
                case PLAYER_ATTACK_UNIT:
                    gUnit.setAttackUnit(unitAim, this, false );
                    break;
                case PLAYER_ATTACK_POS:
                    gUnit.setAttackPos( xp, yp, this, false );
                    break;
                case PLAYER_ADD_ATTACK_UNIT:
                    gUnit.setAttackUnit(unitAim, this, true );
                    break;
                case PLAYER_ADD_ATTACK_POS:
                    gUnit.setAttackPos( xp, yp, this, true );
                    break;
                case PLAYER_LEARN_STATS:
                    gUnit.learnStats( this );
                    break;
                case PLAYER_LEARN_ABILITY:
                    gUnit.learnAbility( num, this );
                    break;
                case PLAYER_CAST_ON_UNIT:
                    gUnit.castSpellOnUnit(num, unitAim, this, false );
                    break;
                case PLAYER_ADD_CAST_ON_UNIT:
                    gUnit.castSpellOnUnit(num, unitAim, this, true );
                    break;
                case PLAYER_AUTO_CAST_ON:
                    gUnit.autoCastON( num, this );
                    break;
                case PLAYER_AUTO_CAST_OFF:
                    gUnit.autoCastOFF( num, this );
                    break;
                case PLAYER_CAST_TO_POS:
                    gUnit.castSpellToPos( num, xp, yp, this, false );
                    break;
                case PLAYER_CAST_ALREADY:
                    gUnit.castSpellAlready( num, this, false );
                    break;
                case PLAYER_ADD_CAST_TO_POS:
                    gUnit.castSpellToPos( num, xp, yp, this, true );
                    break;
                case PLAYER_ADD_CAST_ALREADY:
                    gUnit.castSpellAlready( num, this, true );
                    break;
                case PLAYER_USE_ITEM_ON_UNIT:
                    gUnit.useItemOnUnit(num, unitAim, this, false );
                    break;
                case PLAYER_ADD_USE_ITEM_ON_UNIT:
                    gUnit.useItemOnUnit(num, unitAim, this, true );
                    break;
                case PLAYER_USE_ITEM_TO_POS:
                    gUnit.useItemToPos( num, xp, yp, this, false );
                    break;
                case PLAYER_USE_ITEM_ALREADY:
                    gUnit.useItemAlready( num, this, false );
                    break;
                case PLAYER_ADD_USE_ITEM_TO_POS:
                    gUnit.useItemToPos( num, xp, yp, this, true );
                    break;
                case PLAYER_ADD_USE_ITEM_ALREADY:
                    gUnit.useItemAlready( num, this, true );
                    break;
                case PLAYER_ADD_DROP_ITEM_TO_POS:
                    gUnit.dropItemToPos(num, xp, yp, this, true);
                    break;
                case PLAYER_DROP_ITEM_TO_POS:
                    gUnit.dropItemToPos(num, xp, yp, this, false);
                    break;
                case PLAYER_ADD_DROP_ITEM_TO_UNIT:
                    gUnit.dropItemToUnit(num, unitAim, this, true);
                    break;
                case PLAYER_DROP_ITEM_TO_UNIT:
                    gUnit.dropItemToUnit(num, unitAim, this, false);
                    break;
                case PLAYER_CHANGE_ITEM_SLOT:
                    gUnit.changeItemSlot(xp, yp, this);
                    break;
                case PLAYER_SELECT_UNIT_GRP:
                    gUnit.selectUnitFormGroup( num );
                    break;
                case PLAYER_SELECT_UNIT:
                    gUnit.selectUnit(unitAim, ID );
                    break;
                case PLAYER_BUY_ITEM:
                    Shop s = Shop.getShop(xp);
                    if (s!=null) s.playerBuyItem(this, yp);
                    break;
                case PLAYER_PING_DANGER:
                case PLAYER_PING:
                    Game.map.addEffect(xp, yp, inControl==PLAYER_PING ? EFFECT_PING : EFFECT_DANGER,
                            1f, color, true, VisiblePlayers.playerBit);
                    break;
            }
            isDone = false;
        }
    }

    public void reset() {
        canAutoAttack = true;
        isServer = lock = inUse = isDone = false;
        mainUnit = null;
        owner.playerBit = 1<<ID;
        owner.name = null;
        name = null;
        addr = null;
        port = -1;
    }

    public void disconnect() {
        if (inUse) {
            System.out.println( "Player["+ID+"] "+addr+":"+port+" is disconnected!" );
            if (!Server.start) data[ 3+(ID<<NAME_SHIFT_SIZE) ] = 0;
            inUse = false;
            mainUnit = null;
            owner.name = null;
            name = null;
            addr = null;
            port = -1;
        }
    }

    public void setStartPos(int x, int y) {
        startX = x; startY = y;
    }

    public void Init() {
        port = -1;
        //lock = inUse = isDone = false;
        addr = null;
        Game.trigger.initPlayer( this );
    }
}
