package com.tobias.server.uno.command;

public class Command {

    private String command;

    private CommandType type;
    private String data;

    public Command(CommandType type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getData(){
        return this.data;
    }

    public CommandType getType() {
        return type;
    }

    public String toString() {
        return "TYPE:" + type + " DATA:" + data;
    }
}
