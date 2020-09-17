package com.tobias.uno.card;

import com.tobias.uno.Player;

import java.util.List;

public class Table {
    private Deck deck;
    private Card cardOnTable;

    public Table() {
        this.deck = new Deck();
    }



    public void setCardOnTable(Card card) {
        deck.putBottom(cardOnTable);
        cardOnTable = card;
    }

    public Card getCardOnTable() {
        return cardOnTable;
    }

    public Deck getDeck() {
        return deck;
    }


    public List<Card> deal(Player p, int n) {
        List<Card> cards = deck.draw(n);
        p.addToHand(cards);
        return cards;
    }

    // Used to set the top card when starting the game.
    public void setTopCard() {
        while(cardOnTable == null) {
            Card c = deck.draw(1).get(0);
            if(c.getCardType() == CardType.NORMAL) {
                cardOnTable = c;
                return;
            } else {
                deck.putBottom(c);
            }
        }
    }

}
