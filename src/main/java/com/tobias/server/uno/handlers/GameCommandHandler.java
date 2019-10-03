package com.tobias.server.uno.handlers;

import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.client.UnoClientManager;
import com.tobias.server.uno.command.Command;
import com.tobias.server.uno.command.CommandType;
import com.tobias.uno.GameManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameCommandHandler extends AbstractCommandHandler {

    private GameManager gameManager;
    private UnoClientManager unoClientManager;
    private static final Logger LOGGER = LogManager.getLogger(GameCommandHandler.class.getName());

    public GameCommandHandler(UnoClientManager unoClientManager) {
        this.unoClientManager = unoClientManager;
    }

    @Override
    public void process(Command command, UnoClient unoClient) {
        switch (command.getType()) {
            case GAME_REQUESTCARD:
                // If deck.size < ToInt(command.getData) reshuffle deck and notify client
                // Then send requested card
                if (!gameManager.canDraw(Integer.parseInt(command.getData()))) {
                    gameManager.restockDeckAndShuffle();
                }
                unoClientManager.sendToClient(unoClient, new Command(CommandType.GAME_SETCARD, gameManager.draw(unoClient.getPlayer(), Integer.parseInt(command.getData()))));
                updateGameInfo();
                break;
            default:
                LOGGER.error("Could not process command: " + command.toString() + " which should be sent to client: " + unoClient.getId());
                break;
        }
    }

    @Override
    public void process(Command command) {
        switch (command.getType()) {
            case GAME_START:
                this.gameManager = new GameManager(unoClientManager.getPlayerFromClients());
                gameManager.createNewGame();
                unoClientManager.sendToAllClients(new Command(CommandType.GAME_START, command.getData()));
                updateGameInfo();
                break;
            case GAME_SETCARD:
                for (UnoClient c : unoClientManager.getClients()) {
                    unoClientManager.sendToClient(c, new Command(CommandType.GAME_SETCARD, gameManager.draw(c.getPlayer(), Integer.parseInt(command.getData()))));
                }
                updateGameInfo();
                break;
            default:
                LOGGER.error("Could not process command: " + command.toString());
                break;
        }
    }

    private void updateGameInfo() {
        unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETDECKCOUNT, String.valueOf(gameManager.getDeckCount())));
        unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETCARDSONTABLECOUNT, String.valueOf(gameManager.getCardsOnTableCount())));
        unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETTOPCARD, gameManager.getTopCard()));
        for(UnoClient client : unoClientManager.getClients()) {
            unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETOPPONENTPLAYERCARDCOUNT,client.getId() + ":" + client.getPlayer().getHandCount()));
        }
    }
}
