package com.tobias.server.uno.command;

import com.tobias.server.uno.client.UnoClient;

class CommandEvent {
    private String command;
    private UnoClient unoClient;

    CommandEvent(String command, UnoClient unoClient) {
        this.command = command;
        this.unoClient = unoClient;
    }

    String getStringCommand() {
        return command;
    }

    UnoClient getUnoClient() {
        return unoClient;
    }
}
