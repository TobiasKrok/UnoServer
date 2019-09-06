package com.tobias.game;

import com.tobias.game.card.Card;

import java.util.List;

public class Player {

    private int id;
    private List<Card> hand;

    public Player(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void draw(List<Card> cards){

    }
    protected void clearHand() {
        this.hand.clear();
    }

}
