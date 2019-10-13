package com.tobias.uno;

import com.tobias.uno.card.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class Game {

    private List<Player> players;
    private LinkedList<Player> queue;
    private Table table;
    private boolean inProgress;
    private static final Logger LOGGER = LogManager.getLogger(Game.class.getName());
    private Player currentPlayer;

    Game(List<Player> players) {
        this.queue = new LinkedList<>();
        this.table = new Table();
        this.players = players;
        queue.addAll(players);
    }

    public boolean isInProgress() {
        return this.inProgress;
    }

    void start() {
        this.inProgress = true;
        for (Player p : players) {
            p.setInGame(true);
        }
        table.setTopCard();
        LOGGER.info("Game started with " + players.size() + " players!");
    }
    Player queueNext() {
        if(currentPlayer == null || queue.getLast() == currentPlayer ) {
            this.currentPlayer = queue.get(0);
            return currentPlayer;
        }
        return (currentPlayer = queue.get(queue.indexOf(currentPlayer) + 1));
    }

    Table getTable() {
        return this.table;
    }

    List<Player> getPlayers() {
        return this.players;
    }
}
