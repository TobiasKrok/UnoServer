package com.tobias.uno;

import com.tobias.uno.card.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Game {

    private List<Player> players;
    private Table table;
    private Queue queue;
    private boolean inProgress;
    private static final Logger LOGGER = LogManager.getLogger(Game.class.getName());

    Game(List<Player> players) {
        this.table = new Table();
        this.players = players;
        this.queue = new Queue(players);
    }

    public boolean isInProgress() {
        return this.inProgress;
    }

    void start() {
        this.inProgress = true;
        for (Player p : players) {
            p.setInGame(true);
        }
        //table.setTopCard();
        LOGGER.info("Game started with " + players.size() + " players!");
    }

    Table getTable() {
        return this.table;
    }

    List<Player> getPlayers() {
        return this.players;
    }

    Queue getQueue() {
        return this.queue;
    }
}
