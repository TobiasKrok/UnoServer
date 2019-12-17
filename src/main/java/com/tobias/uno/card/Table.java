package com.tobias.uno.card;

import com.tobias.uno.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Table {
    private Deck deck;
    private List<Card> cardsOnTable;

    public Table() {
        this.cardsOnTable = new ArrayList<>();
        this.deck = new Deck();


    }

    public Card getTopCard() {
        if (cardsOnTable.size() != 0)
            return cardsOnTable.get(cardsOnTable.size() - 1);

        return null;
    }


    public void addCardToTable(Card c) {
        cardsOnTable.add(c);
        if(cardsOnTable.size() > 3) {

        }
    }

    public Deck getDeck() {
        return deck;
    }

    public void addCardToTable(List<Card> cards) {
        cardsOnTable.addAll(cards);
    }

    public List<Card> deal(Player p, int n) {
        List<Card> cards = deck.draw(n);
        p.addToHand(cards);
        return cards;
    }

    public void setTopCard() {
        // TODO yes bad, fix later
        while(true) {
            Card c = deck.getCards().get(deck.getCards().size() - 1);
            if(c.getCardType() == CardType.NORMAL) {
                cardsOnTable.add(c);
                deck.getCards().remove(c);
                return;
            }
            // Move card to the middle of the deck if the card was not a normal card.
            Collections.swap(deck.getCards(),deck.getCards().size() - 1,(deck.getCards().size() - 1) / 2);
        }
    }

    public int getCardsOnTableCount() {
        return cardsOnTable.size();
    }

    public void restockDeck() {
        Iterator iter = cardsOnTable.iterator();
        while (iter.hasNext()) {
            Card c = (Card) iter.next();
            if (c != getTopCard()) {
                deck.add(c);
                iter.remove();
            }
        }
    }
}
