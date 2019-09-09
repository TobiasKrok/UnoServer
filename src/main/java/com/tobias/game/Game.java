package com.tobias.game;

import com.tobias.game.card.Table;
import com.tobias.server.uno.UnoServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<Player> players;
    private GameManager gameManager;
    private Table table;
    private boolean inProgress;
    private static final Logger LOGGER = LogManager.getLogger(Game.class.getName());

    public Game() {
        this.table = new Table();
        this.players = new ArrayList<>();
        this.gameManager = new GameManager(table,players);
    }

    public GameManager getGameManager(){
        return this.gameManager;
    }

    public boolean isInProgress() {
        return this.inProgress;
    }
    public void start() {
        this.inProgress = true;
        LOGGER.info("Game started with " + players.size() + " players!");
    }
    public void setPlayers(List<Player> players) throws InvalidParameterException {
        if(players.size() < UnoServer.maxPlayers) {
            throw new InvalidParameterException("Player size cannot be less than " + UnoServer.maxPlayers + " Current size is " + players.size());
        }
        for(Player p : players) {
            p.clearHand();
        }
        this.players = players;
    }

    protected Table getTable() {
        return this.table;
    }
}
