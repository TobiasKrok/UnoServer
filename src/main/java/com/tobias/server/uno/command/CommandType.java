package com.tobias.server.uno.command;

public enum CommandType {
    GAME_SETCARD,
    GAME_CLIENTDRAWCARD,
    GAME_OPPONENTDRAWCARD,
    GAME_UNO,
    GAME_FORGOTUNO,
    GAME_PLAYERDISCONNECT,
    GAME_START,
    GAME_SETDECKCOUNT,
    GAME_SETOPPONENTPLAYERCARDCOUNT,
    GAME_CLIENTLAYCARD,
    GAME_OPPONENTLAYCARD,
    GAME_SETNEXTTURN,
    GAME_SKIPTURN,
    GAME_SETCOLOR,
    GAME_CLIENTSETCOLOR,
    GAME_FINISHED,


    CLIENT_CONNECT,
    CLIENT_CONNECTED,
    CLIENT_CONNECTEDPLAYERS,
    CLIENT_POLL,
    CLIENT_READY,
    CLIENT_NOTREADY,
    CLIENT_REGISTERID,
    CLIENT_DISCONNECT,


    WORKER_UNKNOWNCOMMAND
}
