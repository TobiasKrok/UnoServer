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
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private UnoClientManager unoClientManager;
    private CommandWorker worker;
    private CommandWorker commandHandlerWorker; //todo local var?
    private boolean running;
    private boolean accepting;
    private int port;

    public UnoServer(int port) {
        this.unoClientManager = new UnoClientManager();
        this.handlers = new HashMap<>();
        this.port = port;
        initializeHandlers();
    }

    public void run() {
        Thread.currentThread().setName("UnoServer-" + Thread.currentThread().getId());
        this.running = true;
        this.accepting = true;
        try (ServerSocket socket = new ServerSocket(this.port)) {
            startPolling();
            startInGameCheck();
            LOGGER.info("Server started on port " + this.port + " with max players allowed: " + minPlayers);
            while (running) {
                if (accepting) {
                    UnoClient unoClient = new UnoClient(socket.accept(), unoClientManager.getClients().size(), new CommandWorker(handlers));
                    LOGGER.info("Client connected: " + unoClient.getIpAddress());
                    initiateClient(unoClient);
                }
            }
        } catch (IOException e) {
            LOGGER.fatal("Server IO exception", e);
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    private void initializeHandlers() {
        ClientCommandHandler clientCommandHandler = new ClientCommandHandler(unoClientManager);
        GameCommandHandler gameCommandHandler = new GameCommandHandler(unoClientManager);
        this.handlers.put("CLIENT", clientCommandHandler);
        this.handlers.put("GAME", gameCommandHandler);
        this.commandHandlerWorker = new CommandWorker(handlers);
        clientCommandHandler.setWorker(commandHandlerWorker);
        gameCommandHandler.setWorker(commandHandlerWorker);
        new Thread(commandHandlerWorker).start();
        this.worker = new CommandWorker(handlers);
        Thread t = new Thread(worker);
        t.setName("CommandWorker-UnoServer-" + t.getId());
        t.start();
    }

    private void initiateClient(UnoClient unoClient) {
        Thread clientThread = new Thread(unoClient);
        clientThread.setName("UnoClient-" + unoClient.getId());
        clientThread.start();
        unoClientManager.addClient(unoClient);
        worker.process(new Command(CommandType.CLIENT_REGISTERID, unoClient.getId()), unoClient);
    }

    private void initializeGame(List<Integer> playerIds) {
        worker.process(new Command(CommandType.GAME_START, playerIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","))));
        for(UnoClient client : unoClientManager.getClients()) {
            worker.process(new Command(CommandType.GAME_CLIENTDRAWCARD,"7"), client);
        }
    }

    private void startInGameCheck() {
        // Schedule check
        ses.scheduleAtFixedRate(() -> {
            if (unoClientManager.getClients().size() == minPlayers) {
                accepting = false;
                if (!unoClientManager.clientsAreInGame()) {
             //       initializeGame(unoClientManager.getPlayerIds());
                }
            } else {
                // If minPlayers has been hit but a client disconnected, we have to set
                // accepting to true so a new player can join.
                accepting = true;
            }
        }, 0, 4, TimeUnit.SECONDS);
    }

    private void startPolling() {
        ses.scheduleAtFixedRate(() -> {
            List<UnoClient> clients = unoClientManager.checkForDisconnect();
            if (clients.size() > 0) {
                for (UnoClient client : clients) {
                    worker.process(new Command(CommandType.GAME_PLAYERDISCONNECT), client);
                    worker.process(new Command(CommandType.CLIENT_DISCONNECT, Integer.toString(client.getId())));
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void setAccepting(boolean val) {
        this.accepting = val;
    }


}
