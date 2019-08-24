package com.tobias.server.uno.command;

import com.tobias.server.uno.client.UnoClient;

public class CommandEvent {
    private String command;
    private UnoClient unoClient;

    public CommandEvent(String command, UnoClient unoClient) {
        this.command = command;
        this.unoClient = unoClient;
    }

    public String getStringCommand() {
        return command;
    }

    public UnoClient getUnoClient() {
        return unoClient;
    }
}
