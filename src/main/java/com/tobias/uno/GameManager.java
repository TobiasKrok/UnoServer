package com.tobias.uno;

import com.tobias.uno.card.Card;
import com.tobias.uno.card.Table;

import java.util.List;

public class GameManager {
    private Game game;
    private Table table;


    public List<Card> draw(Player player, int n) {
        return table.deal(player, n);
    }


    public void restockDeckAndShuffle() {
        table.restockDeck();
        table.getDeck().shuffle();
    }

    public void createNewGame(List<Player> players) {
        this.game = new Game(players);
        this.table = game.getTable();
        game.start();
    }

    public int getDeckCount() {
        return table.getDeck().getDeckCount();
    }
    //TODO fix
    public String getTopCard() {
        if (!(table.getTopCard() == null)) {
            return table.getTopCard().toString();
        }
        // Return blank if null.
        return "";
    }

    public Player nextQueue() {
        return game.getQueue().next();
    }

    public void reverseQueue() {
        game.getQueue().reverse();
    }
    public Player peekNextInQueue(){
        return game.getQueue().peek();
    }

    public void layCard(Player p, Card card) {
        table.addCardToTable(card);
        p.getHand().remove(card);

    }

    public Card getCardByString(Player p, String cardStr) {
        for (Card c : p.getHand()) {
            if (c.toString().equals(cardStr)){
                return c;
            }
        }
        return null;
    }

    public void skipNextInQueue() {
        game.getQueue().toggleSkip();
    }


    public void disconnectPlayer(Player p) {
        table.getDeck().add(p.getHand());
        p.clearHand();
        game.getPlayers().remove(p);
    }
}
