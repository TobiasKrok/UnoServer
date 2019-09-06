package com.tobias.game;

import com.tobias.game.card.Table;

import java.util.List;

public class GameManager {
    private List<Player> players;
    private Table table;

    protected GameManager(Table table, List<Player> players ) {
        this.players = players;
        this.table = table;
    }


    public void draw(Player player, int n) {
        table.deal(player,n);
    }
}
