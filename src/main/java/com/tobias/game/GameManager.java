package com.tobias.game;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Game> games;

    public GameManager() {
        this.games = new ArrayList<>();
    }

    public Game newGame(List<Player> players) {
        Game game = new Game(games.size());
        game.setPlayers(players);
        this.games.add(game);
        return game;
    }

    public Game getGameByPlayer(Player p) {
        for(Game game : games) {
            for(Player player : game.getPlayers()) {
                if (player == p) {
                    return game;
                }
            }
        }
        return null;
    }
}
