package com.tobias.server.uno.client;

import com.tobias.game.Player;
import com.tobias.server.uno.command.CommandWorker;

import java.io.*;
import java.net.Socket;

public class UnoClient implements Runnable {

    private Player player;
    private BufferedReader input;
    private BufferedWriter output;
    private boolean ready;
    private int id;
    private Thread workerThread;
    private CommandWorker worker;
    private String ipAddress;
    private boolean disconnected;

    public UnoClient(Socket socket, int id, CommandWorker worker) {
        this.worker = worker;
        this.id = id;
        this.ready = false;
        this.ipAddress = socket.getRemoteSocketAddress().toString();
        this.disconnected = false;
        try {
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        this.workerThread = new Thread(this.worker);
        workerThread.start();
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
            System.out.println("[CLIENT-" + id + "} - " + "CLOSED SOCKET INPUT/OUTPUT");
            Thread.currentThread().interrupt();
            System.out.println("[CLIENT-" + id + "} - " + "STOPPED THREAD");
            this.disconnected = true;
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

    public boolean isDisconnected() {
        return this.disconnected;
    }
}