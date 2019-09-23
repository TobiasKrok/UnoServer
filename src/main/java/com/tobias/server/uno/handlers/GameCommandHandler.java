package com.tobias.server.uno.handlers;

import com.tobias.game.GameManager;
import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.client.UnoClientManager;
import com.tobias.server.uno.command.Command;
import com.tobias.server.uno.command.CommandType;

public class GameCommandHandler implements CommandHandler {

    private GameManager gameManager;
    private UnoClientManager unoClientManager;
    public GameCommandHandler(GameManager gameManager, UnoClientManager unoClientManager)
    {
        this.gameManager = gameManager;
        this.unoClientManager = unoClientManager;
    }

    @Override
    public void process(Command command, UnoClient unoClient) {
        if(command.getType() == CommandType.GAME_DRAWCARD) {
            unoClientManager.sendToClient(unoClient,new Command(CommandType.GAME_DRAWCARD,gameManager.draw(unoClient.getPlayer(),Integer.parseInt(command.getData()))));
        }
    }
    @Override
    public void process(Command command) {
        if(command.getType() == CommandType.GAME_START) {
            // Pass game ID to client
            unoClientManager.sendToAllClients(new Command(CommandType.GAME_START,command.getData()));
        } else if(command.getType() == CommandType.GAME_DRAWCARD) {
            for (UnoClient c : unoClientManager.getClients()) {
                unoClientManager.sendToClient(c,new Command(CommandType.GAME_DRAWCARD, gameManager.draw(c.getPlayer(),Integer.parseInt(command.getData()))));
            }
        } else if(command.getType() == CommandType.GAME_REGISTEROPPONENTPLAYER) {
            unoClientManager.sendToAllClients(new Command(CommandType.GAME_REGISTEROPPONENTPLAYER,command.getData()));
        }
    }

}
