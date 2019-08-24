package com.tobias.server.uno.handlers;

import com.tobias.game.GameManager;
import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.client.UnoClientManager;
import com.tobias.server.uno.command.Command;

public class GameCommandHandler implements CommandHandler {

    private GameManager gameManager;
    private UnoClientManager unoClientManager;
    public GameCommandHandler(GameManager gameManager, UnoClientManager unoClientManager)
    {
        this.gameManager = gameManager;
    }

    @Override
    public void process(Command command, UnoClient unoClient) {

    }

}
