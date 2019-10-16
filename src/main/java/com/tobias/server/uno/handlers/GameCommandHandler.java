package com.tobias.server.uno.handlers;

import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.client.UnoClientManager;
import com.tobias.server.uno.command.Command;
import com.tobias.server.uno.command.CommandType;
import com.tobias.uno.GameManager;
import com.tobias.uno.Player;
import com.tobias.uno.card.Card;
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
                updateGameInfo(false);
                break;
            case GAME_PLAYERDISCONNECT:
                gameManager.disconnectPlayer(unoClient.getPlayer());
                unoClientManager.sendToAllClients(new Command(CommandType.GAME_PLAYERDISCONNECT, String.valueOf(unoClient.getPlayer().getId())));
                updateGameInfo(true);
                break;
            case GAME_LAYCARD:
                // GAME_LAYCARD should only contain a single card
                handleLayCard(command,unoClient);
                updateGameInfo(true);
                break;
            case GAME_SKIPTURN:
                unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETNEXTTURN, String.valueOf(gameManager.nextQueue().getId())));
                break;
            case GAME_SETCOLOR:
                unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETCOLOR,command.getData()));
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
                this.gameManager = new GameManager();
                gameManager.createNewGame(unoClientManager.getPlayerFromClients());
                unoClientManager.sendToAllClients(new Command(CommandType.GAME_START, command.getData()));
                updateGameInfo(false);
                break;
            case GAME_SETCARD:
                for (UnoClient c : unoClientManager.getClients()) {
                    unoClientManager.sendToClient(c, new Command(CommandType.GAME_SETCARD, gameManager.draw(c.getPlayer(), Integer.parseInt(command.getData()))));
                }
                updateGameInfo(true);
                break;
            default:
                LOGGER.error("Could not process command: " + command.toString());
                break;
        }
    }


    private void updateGameInfo(boolean nextTurn) {
        unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETDECKCOUNT, String.valueOf(gameManager.getDeckCount())));
        unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETCARDSONTABLECOUNT, String.valueOf(gameManager.getCardsOnTableCount())));
        unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETTOPCARD, gameManager.getTopCard()));
        for (UnoClient client : unoClientManager.getClients()) {
            unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETOPPONENTPLAYERCARDCOUNT, client.getId() + ":" + client.getPlayer().getHandCount()));
        }
        // We can update nextTurn here if the player did not need to draw multiple cards.
        if(nextTurn) unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETNEXTTURN, String.valueOf(gameManager.nextQueue().getId())));
    }

    private void handleLayCard(Command command, UnoClient client) {
        Card c;
        if ((c = gameManager.getCardByString(client.getPlayer(), command.getData())) != null) {
            Player nextPlayer = gameManager.peekNextInQueue();
            switch (c.getCardType()) {
                case WILDDRAWFOUR:
                    gameManager.layCard(client.getPlayer(),c);
                    unoClientManager.sendToClient(unoClientManager.getClientByPlayer(nextPlayer),new Command(CommandType.GAME_SETCARD,gameManager.draw(nextPlayer,4)));
                    break;
                case REVERSE:
                    gameManager.layCard(client.getPlayer(),c);
                    gameManager.reverseQueue();
                    break;
                case SKIP:
                    gameManager.layCard(client.getPlayer(),c);
                    gameManager.skipNextInQueue();
                    break;
                case DRAWTWO:
                    gameManager.layCard(client.getPlayer(),c);
                    unoClientManager.sendToClient(unoClientManager.getClientByPlayer(nextPlayer),new Command(CommandType.GAME_SETCARD,gameManager.draw(nextPlayer,2)));
                    break;
                default:
                    gameManager.layCard(client.getPlayer(), c);
            }
        }
    }
}
