package com.tobias.uno.card;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Deck {

    private List<Card> cards;


    public Deck() {
        this.cards = new ArrayList<>();
        createDeck();
        shuffle();
    }

    private void createDeck() {
        // Each color has one instance for 0-9 sequence, one instance of 1-9 sequence and two instances of each action cards.
        // There are four of each special card (Wild and Wild Draw Four)

        // Add 0-9 sequence
        for (CardColor cardColor : CardColor.values()) {
            // This if prevents it from adding a
            if (cardColor != CardColor.SPECIAL && cardColor != CardColor.NONE) {
                for (int i = 0; i < 9; i++) {
                    cards.add(new Card(cardColor, i));
                }
                // Add 1-9 sequence
                for (int i = 1; i < 9; i++) {
                    cards.add(new Card(cardColor, i));
                }
                // Add action cards
                for (CardType cardType : CardType.values()) {
                    // Exclude normal cards and special cards. Special cards do not have a specific color.
                    if (cardType != CardType.NORMAL && cardType != CardType.WILD
                            && cardType != CardType.WILDDRAWFOUR) {
                        cards.add(new Card(cardColor, cardType));
                    }
                }
            }
        }
        // Add special cards
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(CardColor.SPECIAL, CardType.WILDDRAWFOUR));
            cards.add(new Card(CardColor.SPECIAL, CardType.WILD));
        }
    }

    public int getDeckCount() {
        return cards.size();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    void add(List<Card> cards) {
        this.cards.addAll(cards);
    }
    void add(Card c) {
        cards.add(c);
    }

    List<Card> getCards() {
        return this.cards;
    }

    List<Card> draw(int n) {
        List<Card> cards = new ArrayList<>();
        Iterator iter = cards.iterator();
        while (iter.hasNext()) {
            cards.add((Card) iter.next());
            iter.remove();

        }
        return cards;
    }
}
