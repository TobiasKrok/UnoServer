package com.tobias.server.uno.command;

public enum CommandType {
    PLAYER_DRAWCARD,
    PLAYER_LAYCARD,
    PLAYER_UNO,
    PLAYER_DISCONNECT,
    CLIENT_CONNECT,
    CLIENT_GAMESTART,
    CLIENT_REGISTEROPPONENTPLAYER,
    CLIENT_POLL,
    CLIENT_READY,
    CLIENT_REGISTERID,
    CLIENT_DISCONNECT,
    WORKER_UNKNOWNCOMMAND
}
