package com.tobias.game;

import com.tobias.game.card.Card;
import com.tobias.game.card.Table;
import com.tobias.server.uno.UnoServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
    private List<Player> players;
    private int gameId;
    private Table table;
    private boolean inProgress;
    private static final Logger LOGGER = LogManager.getLogger(Game.class.getName());

    public Game(int gameId) {
        this.table = new Table();
        this.players = new ArrayList<>();
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

    public String draw(Player player, int n) {
        List<Card> cards = table.deal(player,n);
        return cards.stream()
                .map(Card::toString)
                .collect(Collectors.joining(","));
    }

    protected Table getTable() {
        return this.table;
    }
}
