package com.tobias.uno;

import com.tobias.uno.card.Card;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private int id;
    private String username;
    private List<Card> hand;
    private boolean isInGame;

    public Player(int id) {
        this.hand = new ArrayList<>();
        this.id = id;
    }

    void setInGame(boolean val) {
        this.isInGame = val;
    }

    public boolean isInGame() {
        return this.isInGame;
    }

    public int getId(){
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void addToHand(List<Card> cards){
        this.hand.addAll(cards);
    }

    void clearHand() {
        this.hand.clear();
    }

    public int getHandCount() {
        return hand.size();
    }
    List<Card> getHand() {
        return hand;
    }
}
