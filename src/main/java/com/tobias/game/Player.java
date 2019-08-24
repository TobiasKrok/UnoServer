package com.tobias.game;

import com.tobias.game.card.Card;
import com.tobias.game.card.Table;

import java.util.List;

public class Player {

    private int id;
    private Table table;
    private List<Card> hand;

    public Player(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
    public void setTableInstance(Table t){
        this.table = t;
    }

    public void draw(List<Card> cards){

    }
    public void draw(int n){

    }

}
