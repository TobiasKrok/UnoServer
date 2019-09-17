package com.tobias.server.uno.command;

import com.tobias.server.uno.client.UnoClient;

class CommandEvent {
    private Object command;
    private UnoClient unoClient;

    CommandEvent(String command, UnoClient unoClient) {
        this.command = command;
        this.unoClient = unoClient;
    }

    CommandEvent(Command command, UnoClient unoClient) {
        this.command = command;
        this.unoClient = unoClient;
    }
    CommandEvent(Command command) {
        this.command = command;
        this.unoClient = null;
    }

    Object getCommand() {
        return command;
    }


    UnoClient getUnoClient() {
        return unoClient;
    }
}
