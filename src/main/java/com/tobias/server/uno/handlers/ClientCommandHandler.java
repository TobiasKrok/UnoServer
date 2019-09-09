package com.tobias.server.uno.handlers;

import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.client.UnoClientManager;
import com.tobias.server.uno.command.Command;
import com.tobias.server.uno.command.CommandType;

public class ClientCommandHandler implements CommandHandler {

    private UnoClientManager clientManager;

    public ClientCommandHandler(UnoClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void process(Command command, UnoClient unoClient) {
        if(command.getType() == CommandType.CLIENT_DISCONNECT) {
            handleClientDisconnect(command.getData());
        } else if(command.getType() == CommandType.CLIENT_REGISTERID) {
            clientManager.sendToClient(unoClient,command);
        }
    }

    private void handleClientDisconnect(String clientId) {
        clientManager.sendToAllClients(new Command(CommandType.CLIENT_DISCONNECT,clientId));
    }
}
