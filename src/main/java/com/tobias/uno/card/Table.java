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

    public Card getCurrentCard(){
        return cardsOnTable.get(cardsOnTable.size() - 1);
    }


    public void addCardToTable(Card c){
        cardsOnTable.add(c);
    }

    public Deck getDeck() {
        return deck;
    }

    public void addCardToTable(List<Card> cards){
        cardsOnTable.addAll(cards);
    }

    public List<Card> deal(Player p, int n){
        List<Card> cards = deck.draw(n);
        p.addToHand(cards);
        return cards;
    }

   public void restockDeck() {
        Iterator iter = cardsOnTable.iterator();
        while (iter.hasNext()) {
            Card c = (Card) iter.next();
            if(c != getCurrentCard()) {
                deck.add(c);
                iter.remove();
            }
        }
    }

}
