package com.tobias.game;

import com.tobias.game.card.Card;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private int id;
    private List<Card> hand;

    public Player(int id) {
        this.hand = new ArrayList<>();
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void addToHand(List<Card> cards){
        this.hand.addAll(cards);
    }
    protected void clearHand() {
        this.hand.clear();
    }

}
