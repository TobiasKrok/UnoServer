package com.tobias.game;

import com.tobias.game.card.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Game {
    private List<Player> players;
    private GameManager gameManager;
    private Table table;
    private boolean inProgress;
    private static final Logger LOGGER = LogManager.getLogger(Game.class.getName());

    public Game(List<Player> players) {
        this.table = new Table();
        this.players = players;
        this.gameManager = new GameManager(this.table,this.players);
    }



    public boolean isInProgress() {
        return this.inProgress;
    }
    public void start() {
        this.inProgress = true;
        LOGGER.info("Game started with " + players.size() + " players!");
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    protected Table getTable() {
        return this.table;
    }

    protected List<Player> getPlayers() {
        return this.players;
    }
}
