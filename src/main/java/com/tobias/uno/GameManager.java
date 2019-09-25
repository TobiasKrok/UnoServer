package com.tobias.uno;

import com.tobias.uno.card.Card;
import com.tobias.uno.card.Table;

import java.util.List;
import java.util.stream.Collectors;

public class GameManager {
    private Game game;
    private Table table;
    private List<Player> players;

    public GameManager(List<Player> players) {
        this.players = players;
    }

    public String draw(Player player, int n) {
        List<Card> cards = table.deal(player,n);
        return cards.stream()
                .map(Card::toString)
                .collect(Collectors.joining(","));
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void createNewGame() {
        this.game = new Game(players);
        this.table = game.getTable();
        game.start();
    }
}
