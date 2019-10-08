package com.tobias.uno.card;

import com.tobias.uno.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Table {
    private Deck deck;
    private List<Card> cardsOnTable;

    public Table() {
        this.cardsOnTable = new ArrayList<>();
        this.deck = new Deck();
        // deck.draw(cardsOnTable,1);


    }

    public Card getTopCard() {
        if (cardsOnTable.size() != 0)
            return cardsOnTable.get(cardsOnTable.size() - 1);

        return null;
    }


    public void addCardToTable(Card c) {
        cardsOnTable.add(c);
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
        Card c;
        // TODO fix this if lol
        if (!((c = deck.get(deck.getCards().size() - 1)) == null)) {
            if ((c.getCardType() == CardType.NORMAL)) {
                cardsOnTable.add(deck.get(deck.getCards().size() - 1));
            }
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
