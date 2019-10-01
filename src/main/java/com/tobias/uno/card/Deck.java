package com.tobias.uno.card;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> deck;
    private List<Card> drawnCards;


    public Deck() {
        this.deck = new ArrayList<>();
        this.drawnCards = new ArrayList<>();
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
                    deck.add(new Card(cardColor, i));
                }
                // Add 1-9 sequence
                for (int i = 1; i < 9; i++) {
                    deck.add(new Card(cardColor, i));
                }
                // Add action cards
                for (CardType cardType : CardType.values()) {
                    // Exclude normal cards and special cards. Special cards do not have a specific color.
                    if (cardType != CardType.NORMAL && cardType != CardType.WILD
                            && cardType != CardType.WILDDRAWFOUR) {
                        deck.add(new Card(cardColor, cardType));
                    }
                }
            }
        }
        // Add special cards
        for (int i = 0; i < 4; i++) {
            deck.add(new Card(CardColor.SPECIAL, CardType.WILDDRAWFOUR));
            deck.add(new Card(CardColor.SPECIAL, CardType.WILD));
        }
    }

    public int getDeckCount() {
        return deck.size();
    }

    void shuffle() {
        Collections.shuffle(deck);
    }

    public void printDeck() {
        for (Card c : deck) {
            System.out.println(c.toString());
        }
    }

    List<Card> getDrawnCards() {
        return this.drawnCards;
    }
    List<Card> draw(int n) {
        List<Card> cards = new ArrayList<>();
        if (n < deck.size() && n > 0) {
            for (int i = 0; i < n; i++) {
                cards.add(deck.get(i));
                drawnCards.add(deck.get(i));
                deck.remove(i);
            }
        }
        return cards;
    }
}
