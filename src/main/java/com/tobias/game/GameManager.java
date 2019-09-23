package com.tobias.game;

import com.tobias.game.card.Card;
import com.tobias.game.card.Table;

import java.util.List;
import java.util.stream.Collectors;

public class GameManager {
    private Table table;
    private List<Player> players;

    public GameManager(Table table, List<Player> players) {
        this.table = table;
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

}
