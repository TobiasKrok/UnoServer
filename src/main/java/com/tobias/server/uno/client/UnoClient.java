package com.tobias.server.uno.client;

import com.tobias.game.Player;
import com.tobias.server.uno.command.CommandWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOGGER = LogManager.getLogger(UnoClient.class.getName());

    public UnoClient(Socket socket, int id, CommandWorker worker) {
        this.worker = worker;
        this.id = id;
        this.ready = false;
        this.player = new Player(this.id);
        this.ipAddress = socket.getRemoteSocketAddress().toString();
        this.disconnected = false;
        try {
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
           LOGGER.error("Failed initializing BufferedReader/Writer",e);
        }
    }

    public void run() {
        this.workerThread = new Thread(this.worker);
        this.workerThread.setName("CommandWorker-" + Integer.toString(this.id));
        workerThread.start();
        LOGGER.debug("Worker for Client " + id + " started!");
        while (!disconnected) {
            try {
                if (input.ready()) {
                    worker.process(read(), this);
                } else
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LOGGER.error("Thread.sleep interrupted", e);
                    }
            } catch (IOException e) {
                LOGGER.error("Error reading input",e);
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
            LOGGER.fatal("Error on reading input!",e);
        }
        return null;
    }

    void close() {
        workerThread.interrupt();
        this.disconnected = true;
        Thread.currentThread().interrupt();
        LOGGER.warn("Client " + id + " gracefully disconnected.");
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