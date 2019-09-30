package com.tobias.server.uno;


import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.client.UnoClientManager;
import com.tobias.server.uno.command.Command;
import com.tobias.server.uno.command.CommandType;
import com.tobias.server.uno.command.CommandWorker;
import com.tobias.server.uno.handlers.AbstractCommandHandler;
import com.tobias.server.uno.handlers.ClientCommandHandler;
import com.tobias.server.uno.handlers.GameCommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UnoServer implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(UnoServer.class.getName());
    public static int minPlayers;
    private Map<String, AbstractCommandHandler> handlers;
    private UnoClientManager unoClientManager;
    private CommandWorker worker;
    private boolean running;
    private boolean accepting;
    private int port;

    public UnoServer(int port) {
        this.unoClientManager = new UnoClientManager();
        this.handlers = new HashMap<>();
        this.port = port;
        this.handlers.put("CLIENT", new ClientCommandHandler(this.unoClientManager));
        this.handlers.put("GAME", new GameCommandHandler(this.unoClientManager));
        this.worker = new CommandWorker(handlers);
        Thread t = new Thread(worker);
        t.setName("CommandWorker-UnoServer-" + t.getId());
        t.start();
    }

    public void run() {
        Thread.currentThread().setName("UnoServer-" + Thread.currentThread().getId());
        this.running = true;
        this.accepting = true;
        try (ServerSocket socket = new ServerSocket(this.port)) {
            startPolling();
            LOGGER.info("Server started on port " + this.port + " with max players allowed: " + minPlayers);
            while (running) {
                if (accepting) {
                    UnoClient unoClient = new UnoClient(socket.accept(), unoClientManager.getClients().size(), new CommandWorker(handlers));
                    LOGGER.info("Client connected: " + unoClient.getIpAddress());
                    initiateClient(unoClient);
                }
                if (unoClientManager.getClients().size() == minPlayers) {
                    accepting = false;
                    if (!unoClientManager.clientsAreInGame()) {
                        initializeGame(unoClientManager.getPlayerIds());
                    }
                } else {
                    // If minPlayers has been hit but a client disconnected, we have to set
                    // accepting to true so a new player can join.
                    accepting = true;
                }
            }
        } catch (IOException e) {
            LOGGER.fatal("Server IO exception", e);
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    private void initiateClient(UnoClient unoClient) {
        Thread clientThread = new Thread(unoClient);
        clientThread.setName("UnoClient-" + unoClient.getId());
        clientThread.start();
        unoClientManager.addClient(unoClient);
        worker.process(new Command(CommandType.CLIENT_REGISTERID, Integer.toString(unoClient.getId())), unoClient);
    }

    private void initializeGame(List<Integer> playerIds) {
        unoClientManager
        worker.process(new Command(CommandType.GAME_START,playerIds.stream().map(String::valueOf).collect(Collectors.joining(","))));
      //  worker.process(new Command(CommandType.GAME_REGISTEROPPONENTPLAYER, ));
        worker.process(new Command(CommandType.GAME_SETCARD, "7"));
    }

    private void startPolling() {
        ScheduledExecutorService ses;
        ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<UnoClient> clients = unoClientManager.checkForDisconnect();
                if (clients.size() > 0) {
                    for (UnoClient client : clients) {
                        // handlers.get("PLAYER").process(new Command(CommandType.PLAYER_DISCONNECT,Integer.toString(client.getId())),client);
                        worker.process(new Command(CommandType.CLIENT_DISCONNECT, Integer.toString(client.getId())));
                    }
                }
            }
        }, 0, 8, TimeUnit.SECONDS);
    }

    public void setAccepting(boolean val) {
        this.accepting = val;
    }


}
