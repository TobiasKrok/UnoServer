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

import java.util.List;
import java.util.stream.Collectors;

public class GameCommandHandler extends AbstractCommandHandler {

    private static final Logger LOGGER = LogManager.getLogger(GameCommandHandler.class.getName());
    private GameManager gameManager;
    private UnoClientManager unoClientManager;

    public GameCommandHandler(UnoClientManager unoClientManager) {
        this.unoClientManager = unoClientManager;
    }

    @Override
    public void process(Command command, UnoClient unoClient) {
        switch (command.getType()) {
            case GAME_CLIENTDRAWCARD:
                List<Card> cards = gameManager.draw(unoClient.getPlayer(), Integer.parseInt(command.getData()));
                String cardStr = cardListToString(cards);
                unoClientManager.sendToClient(unoClient, new Command(CommandType.GAME_SETCARD, cardStr));
                sendToAllClientsExclude(unoClient, new Command(CommandType.GAME_OPPONENTDRAWCARD, String.valueOf(unoClient.getId()) + ":" + String.valueOf(cards.size())));
                updateGameInfo(false);
                break;
            case GAME_PLAYERDISCONNECT:
                gameManager.disconnectPlayer(unoClient.getPlayer());
                unoClientManager.sendToAllClients(new Command(CommandType.GAME_PLAYERDISCONNECT, String.valueOf(unoClient.getId())));
                updateGameInfo(true);
                break;
            case GAME_CLIENTLAYCARD:
                // GAME_CLIENTLAYCARD should only contain a single card
                handleLayCard(command, unoClient);
                sendToAllClientsExclude(unoClient, new Command(CommandType.GAME_OPPONENTLAYCARD, String.valueOf(unoClient.getId() + ":" + command.getData())));
                if(unoClient.getPlayer().getHandCount() == 0) {
                    unoClientManager.sendToAllClients(new Command(CommandType.GAME_FINISHED,String.valueOf(unoClient.getId())));
                }
                updateGameInfo(true);
                break;
            case  GAME_SKIPTURN:
                updateGameInfo(true);
           //     unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETNEXTTURN, String.valueOf(gameManager.nextQueue().getId())));
                break;
            case GAME_CLIENTSETCOLOR:
                unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETCOLOR, command.getData()));
                break;
            case GAME_UNO:
                // If the data is empty, it means that a client with 2 cards has said uno before he layed
                if(command.getData().isEmpty()) {
//                    for(UnoClient client : unoClientManager.getClients()) {
//                        // if one player has said uno then the client was too late and must draw 3 cards
//                        if(client.hasSaidUno()) {
//                            unoClientManager.sendToClient(client, );
//                            hasSaidUno = true;
//                        }
                    // Notify all clients that the client has said uno
                    sendToAllClientsExclude(unoClient,new Command(CommandType.GAME_UNO,String.valueOf(unoClient.getId())));
                    //}

                }
                break;
            case GAME_FORGOTUNO:
                // The client forgot to press the UNO button. Notify all other clients.
                if(command.getData().isEmpty()) {
                    gameManager.setForgotUnoPlayer(unoClient.getPlayer());
                } else if(command.getData().equals("OPPONENT")) {
                    // A player has pressed the forgot uno button after a client has forgotten to say UNO.
                    sendToAllClientsExclude(unoClient, new Command(CommandType.GAME_FORGOTUNO, String.valueOf(unoClient.getId())));
                    for(UnoClient client : unoClientManager.getClients()) {
                        if(client.getPlayer() == gameManager.getForgotUnoPlayer()) {
                            unoClientManager.sendToClient(client, new Command(CommandType.GAME_CLIENTDRAWCARD, String.valueOf(3)));
                        }
                    }
                    // Reset forgotUnoPlayer
                    gameManager.setForgotUnoPlayer(null);
                }
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
                System.out.println(gameManager.getTopCard());
                unoClientManager.sendToAllClients(new Command(CommandType.GAME_START, command.getData() + ":" + gameManager.getTopCard().toString()));
                updateGameInfo(true);
                break;
            case GAME_SETCARD:
                for (UnoClient c : unoClientManager.getClients()) {
                    unoClientManager.sendToClient(c, new Command(CommandType.GAME_SETCARD, cardListToString(gameManager.draw(c.getPlayer(), Integer.parseInt(command.getData())))));
                }
               // updateGameInfo(true);
                break;
            default:
                LOGGER.error("Could not process command: " + command.toString());
                break;
        }
    }

    // Sends to all clients except the passed client
    private void sendToAllClientsExclude(UnoClient client, Command command) {
        for (UnoClient c : unoClientManager.getClients()) {
            if (c != client) {
                unoClientManager.sendToClient(c, command);
            }
        }
    }

    private void updateGameInfo(boolean nextTurn) {
        unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETDECKCOUNT, String.valueOf(gameManager.getDeckCount())));
        for (UnoClient client : unoClientManager.getClients()) {
            unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETOPPONENTPLAYERCARDCOUNT, client.getId() + ":" + client.getPlayer().getHandCount()));
        }
        // We can update nextTurn here if the player did not need to draw multiple cards.
        if (nextTurn) {
            unoClientManager.sendToAllClients(new Command(CommandType.GAME_SETNEXTTURN, String.valueOf(gameManager.nextQueue().getId())));
        }
    }

    private void handleLayCard(Command command, UnoClient client) {
        Card c;
        if ((c = gameManager.getCardByString(client.getPlayer(), command.getData())) != null) {
            Player nextPlayer = gameManager.peekNextInQueue();
            switch (c.getCardType()) {
                case WILDDRAWFOUR:
                    gameManager.layCard(client.getPlayer(), c);
                    // Call GAME_CLIENTDRAWCARD procedure and drawing 4 cards.
                    process(new Command(CommandType.GAME_CLIENTDRAWCARD, Integer.toString(4)), unoClientManager.getClientByPlayer(nextPlayer));
                    break;
                case REVERSE:
                    gameManager.layCard(client.getPlayer(), c);
                    gameManager.reverseQueue();
                    break;
                case SKIP:
                    gameManager.layCard(client.getPlayer(), c);
                    gameManager.skipNextInQueue();
                    break;
                case DRAWTWO:
                    gameManager.layCard(client.getPlayer(), c);
                    // Call GAME_CLIENTDRAWCARD procedure and drawing 2 cards.
                    process(new Command(CommandType.GAME_CLIENTDRAWCARD, Integer.toString(2)), unoClientManager.getClientByPlayer(nextPlayer));
                    break;
                default:
                    gameManager.layCard(client.getPlayer(), c);
            }
        }
    }

    private String cardListToString(List<Card> cards) {
        return cards.stream()
                .map(Card::toString)
                .collect(Collectors.joining(","));
    }
}
