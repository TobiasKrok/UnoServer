package com.tobias.server.uno.command;

import com.tobias.server.uno.client.UnoClient;

class CommandEvent {
    private Command command;
    private UnoClient unoClient;

    CommandEvent(Command command, UnoClient unoClient) {
        this.command = command;
        this.unoClient = unoClient;
    }

    Command getCommand() {
        return command;
    }


    UnoClient getUnoClient() {
        return unoClient;
    }
}
