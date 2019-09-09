package com.tobias.game.card;

import com.tobias.game.Player;

import java.util.ArrayList;
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
        // Resets color
        // GameController.setLegalCardColor(0);
    }
    public void addCardToTable(List<Card> cards){
        cardsOnTable.addAll(cards);
    }

    public List<Card> deal(Player p, int n){
        if(deck.getDeckCount() >= n) {
            List<Card> cards = deck.draw(n);
            p.addToHand(cards);
            // Return cards so we can inform client
            return cards;
        } else {
            //TODO shuffle cards on table and put back in deck
        }
        return null;
    }

}
