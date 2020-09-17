package com.tobias.uno;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class Queue {
    private LinkedList<Player> queue;
    private Player currentPlayer;
    private boolean skipNext;

    Queue(List<Player> players) {
        this.queue = new LinkedList<>();
        this.queue.addAll(players);
    }

    Player next() {
        // If we only have two players, it's the player who laid the SKIP card turn again.
        if(queue.size() == 2 && skipNext){
            skipNext = false;
            return currentPlayer;
        }

       else if (currentPlayer == null || queue.getLast() == currentPlayer) {
            currentPlayer = skipNext ? queue.get(1) : queue.get(0);
            skipNext = false;
            return currentPlayer;
        }
        currentPlayer = skipNext ? queue.get(queue.indexOf(currentPlayer) + 2) : queue.get(queue.indexOf(currentPlayer) + 1);
        skipNext = false;
        return  currentPlayer;
    }

    Player peek() {
        if(queue.size() == 2 && skipNext){
            return currentPlayer;
        }
        if (currentPlayer == null || queue.getLast() == currentPlayer) {
            return skipNext ? queue.get(1) : queue.get(0);
        }
        return skipNext ? queue.get(queue.indexOf(currentPlayer) + 2) : queue.get(queue.indexOf(currentPlayer) + 1);
    }

    Player current() {
        return currentPlayer;
    }

    void toggleSkip() {
        //Toggles skip to true so next() will skip a player
        this.skipNext = true;
    }

    void reverse() {
        // If we only have two players, a REVERSE card acts like a skip.
        if(queue.size() == 2) toggleSkip();

        else Collections.reverse(queue);
    }

}

