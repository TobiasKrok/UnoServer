package com.tobias.server.uno.handlers;

import com.tobias.uno.GameManager;
import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.client.UnoClientManager;
import com.tobias.server.uno.command.Command;
import com.tobias.server.uno.command.CommandType;

public class GameCommandHandler extends AbstractCommandHandler {

    private GameManager gameManager;
    private UnoClientManager unoClientManager;
    public GameCommandHandler(UnoClientManager unoClientManager)
    {
        this.unoClientManager = unoClientManager;
    }

    @Override
    public void process(Command command, UnoClient unoClient) {
        if(command.getType() == CommandType.GAME_REQUESTCARD) {
            unoClientManager.sendToClient(unoClient,new Command(CommandType.GAME_SETCARD,gameManager.draw(unoClient.getPlayer(),Integer.parseInt(command.getData()))));
        }
    }
    @Override
    public void process(Command command) {
        if(command.getType() == CommandType.GAME_START) {
            this.gameManager = new GameManager(unoClientManager.getPlayerFromClients());
            gameManager.createNewGame();
            unoClientManager.sendToAllClients(new Command(CommandType.GAME_START,command.getData()));
        } else if(command.getType() == CommandType.GAME_REQUESTCARD) {
            for (UnoClient c : unoClientManager.getClients()) {
                unoClientManager.sendToClient(c,new Command(CommandType.GAME_SETCARD, gameManager.draw(c.getPlayer(),Integer.parseInt(command.getData()))));
            }
        } else if(command.getType() == CommandType.GAME_REGISTEROPPONENTPLAYER) {
            unoClientManager.sendToAllClients(new Command(CommandType.GAME_REGISTEROPPONENTPLAYER,command.getData()));
        }
    }

}
