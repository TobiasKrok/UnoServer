package com.tobias.server.uno.client;

import com.tobias.game.Player;
import com.tobias.server.uno.command.CommandWorker;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UnoClient implements Runnable{

    private Player player;
    private BufferedReader input;
    private BufferedWriter output;
    private boolean ready;
    private int id;
    private List<Integer> usedId;
    private CommandWorker worker;

    public UnoClient(Socket socket, int id, CommandWorker worker) {
        this.usedId = new ArrayList<>();
        this.worker = worker;
        this.id = id;
        this.ready = false;
        try {
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("UnoClient " + id + " connected");
    }

    public void run() {
        new Thread(this.worker).start();
        while (true) {
            try {
                if (input.ready()) {
                    worker.processCommand(read(), this);
                }
            } catch (IOException e) {


            }
        }
    }
    public boolean isReady() {
        return this.ready;
    }

    public void setReady() {
        this.ready = true;
    }
    private String read() {
        try {
            return input.readLine();

        } catch (IOException e) {
            System.out.println("GameServer read error: " + e.getMessage());
        }
        return null;
    }

    private void close() {
        try {
            output.close();
            input.close();
        } catch (IOException e) {
            System.out.println("Input/Output close error: " + e.getMessage());
        }
    }

    void write(String message, int msgId) {
        try {
            output.write("ID:" + msgId + " CMD:" + message);

        } catch (IOException e) {
            System.out.println("GameServer write error: " + e.getMessage());
        } finally {
            try {
                output.newLine();
                output.flush();
            } catch (IOException e) {
                System.out.println("Output close error: " + e.getMessage());
            }

        }
    }

    void write(String message) {
        Random random = new Random();
        write(message, random.nextInt());
    }
    public Player getPlayer(){
        return this.player;
    }
}