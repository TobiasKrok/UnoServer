package com.tobias.game;

import com.tobias.game.card.Table;

import java.security.InvalidParameterException;
import java.util.List;

public class Game {
    private List<Player> players;
    private GameManager gameManager;
    private Table table;

    public Game() {
        this.table = new Table();
        this.gameManager = new GameManager();
    }

    public GameManager getGameManager(){
        return this.gameManager;
    }

    public void setPlayers(List<Player> players) throws InvalidParameterException {
        if(players.size() < 4) {
            throw new InvalidParameterException("Player size cannot be less than 4. Current size is " + players.size());
        }
        this.players = players;
    }
}
