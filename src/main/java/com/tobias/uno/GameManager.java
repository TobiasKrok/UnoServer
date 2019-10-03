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

    public boolean canDraw(int n) {
        return (table.getDeck().getDeckCount() >= n);
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

    public void restockDeckAndShuffle() {
        table.restockDeck();
        table.getDeck().shuffle();
    }

    public void createNewGame() {
        this.game = new Game(players);
        this.table = game.getTable();
        game.start();
    }

    public int getDeckCount() {
        return table.getDeck().getDeckCount();
    }
    public int getCardsOnTableCount() {
        return table.getCardsOnTableCount();
    }
    public String getTopCard() {
        return table.getTopCard().toString();
    }
}
