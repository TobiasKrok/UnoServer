package com.tobias.server.uno.client;

import com.tobias.game.Player;
import com.tobias.server.uno.command.CommandWorker;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UnoClient implements Runnable {

    private Player player;
    private BufferedReader input;
    private BufferedWriter output;
    private boolean ready;
    private int id;
    private List<Integer> usedId;
    private CommandWorker worker;
    private String ipAddress;

    public UnoClient(Socket socket, int id, CommandWorker worker) {
        this.usedId = new ArrayList<>();
        this.worker = worker;
        this.id = id;
        this.ready = false;
        this.ipAddress = socket.getRemoteSocketAddress().toString();
        try {
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        Thread t = new Thread(this.worker);
        t.start();
        while (true) {
            try {
                if (input.ready()) {
                    worker.processCommand(read(), this);
                } else
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("input.ready() thread sleep interrupted: " + e.getMessage());
                    }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public boolean isReady() {
        return this.ready;
    }

    public String getIpAddress() {
        return this.ipAddress;
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

    void close() {
        try {
            output.close();
            input.close();
        } catch (IOException e) {
            System.out.println("Input/Output close error: " + e.getMessage());
        }
    }

    void write(String command) throws IOException {
        output.write(command);
        output.newLine();
        output.flush();
    }


    public Player getPlayer() {
        return this.player;
    }

    public int getId() {
        return this.id;
    }
}