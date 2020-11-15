package com.tobias.server.uno.handlers;

import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.client.UnoClientManager;
import com.tobias.server.uno.command.Command;
import com.tobias.server.uno.command.CommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

public class ClientCommandHandler extends AbstractCommandHandler {

    private UnoClientManager clientManager;
    private static final Logger LOGGER = LogManager.getLogger(ClientCommandHandler.class.getName());

    public ClientCommandHandler(UnoClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void process(Command command, UnoClient unoClient) {
        switch (command.getType()) {
            case CLIENT_REGISTERID:
                clientManager.sendToClient(unoClient, command);
                break;
            case CLIENT_CONNECT:
                unoClient.getPlayer().setUsername(command.getData());
                String connectedPlayers = clientManager.getClients()
                        .stream()
                        .filter(e -> e.getPlayer().getUsername() != null)
                        .map(e -> "[" + e.getId() + ":" + e.getPlayer().getUsername() + ":" + e.isReady() + "]")
                        .collect(Collectors.joining(","));
                // Update  the connected player with the names and ID's of all other connected players
                clientManager.sendToAllClients(new Command(CommandType.CLIENT_CONNECTED, unoClient.getId() + ":" + unoClient.getPlayer().getUsername() + ":" + unoClient.isReady()));
                clientManager.sendToClient(unoClient, new Command(CommandType.CLIENT_CONNECTEDPLAYERS, connectedPlayers));
                break;
            case CLIENT_READY:
                unoClient.setReady(true);
                sendToAllClientsExclude(unoClient, new Command(CommandType.CLIENT_READY, unoClient.getId()));
                boolean allReady = clientManager.getClients().stream()
                        .allMatch(UnoClient::isReady);
                // If everyone is ready, start up the game
                if(allReady) {
                    worker.process(new Command(CommandType.GAME_START, clientManager.getPlayerIds().stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","))));
                }
                break;
            case CLIENT_NOTREADY:
                unoClient.setReady(false);
                sendToAllClientsExclude(unoClient, new Command(CommandType.CLIENT_NOTREADY, unoClient.getId()));
                break;
            default:
                LOGGER.error("Could not process command: " + command.toString() + " which should be sent to client: " + unoClient.getId());
        }
    }

    @Override
    public void process(Command command) {
        switch (command.getType()) {
            case CLIENT_DISCONNECT:
                clientManager.sendToAllClients(new Command(CommandType.CLIENT_DISCONNECT, command.getData()));
                break;
            default:
                LOGGER.error("Could not process command: " + command.toString());
        }
    }

    // Sends to all clients except the passed client
    private void sendToAllClientsExclude(UnoClient client, Command command) {
        for (UnoClient c : clientManager.getClients()) {
            if (c != client) {
                clientManager.sendToClient(c, command);
            }
        }
    }
}
