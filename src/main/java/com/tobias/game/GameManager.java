package com.tobias.game;

import com.tobias.game.card.Card;
import com.tobias.game.card.Table;

import java.util.List;
import java.util.stream.Collectors;

public class GameManager {
    private List<Player> players;
    private Table table;

    protected GameManager(Table table, List<Player> players ) {
        this.players = players;
        this.table = table;
    }


    public String draw(Player player, int n) {
       List<Card> cards = table.deal(player,n);
       return cards.stream()
               .map(Card::toString)
               .collect(Collectors.joining(","));
    }
}
