package com.tobias.uno;

import com.tobias.uno.card.Card;
import com.tobias.uno.card.Table;

import java.util.List;

public class GameManager {
    private Game game;
    private Table table;
    // Used to store the player that has forgotten to say Uno.
    private Player forgotUnoPlayer;


    public List<Card> draw(Player player, int n) {
        return table.deal(player, n);
    }

    public Card getTopCard() {
     return table.getCardOnTable();
    }
    public void createNewGame(List<Player> players) {
        this.game = new Game(players);
        this.table = game.getTable();
        table.setTopCard();
        game.start();
    }

    public int getDeckCount() {
        return table.getDeck().getDeckCount();
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
        table.setCardOnTable(card);
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

    public Player getForgotUnoPlayer() {
        return forgotUnoPlayer;
    }
    public void setForgotUnoPlayer(Player player) {
        this.forgotUnoPlayer = player;
    }
    public void disconnectPlayer(Player p) {
        table.getDeck().add(p.getHand());
        p.clearHand();
        game.getPlayers().remove(p);
    }
}
