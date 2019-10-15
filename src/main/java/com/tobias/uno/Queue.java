package com.tobias.uno;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class Queue {
    private LinkedList<Player> queue;
    private Player currentPlayer;

    Queue(List<Player> players) {
        this.queue = new LinkedList<>();
        this.queue.addAll(players);
    }

    Player next() {
        if (currentPlayer == null || queue.getLast() == currentPlayer) {
            this.currentPlayer = queue.get(0);
            return currentPlayer;
        }
        return (currentPlayer = queue.get(queue.indexOf(currentPlayer) + 1));
    }

    Player peek() {
        if (currentPlayer == null || queue.getLast() == currentPlayer) {
            return queue.get(0);
        }
        return queue.get(queue.indexOf(currentPlayer) + 1);
    }

    void reverse() {
        Collections.reverse(queue);
    }
    //https://repl.it/repls/AshamedOffensiveCharactermapping
}

