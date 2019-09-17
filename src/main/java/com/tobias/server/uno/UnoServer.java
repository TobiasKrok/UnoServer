package com.tobias.server.uno;


import com.tobias.game.Game;
import com.tobias.game.GameManager;
import com.tobias.game.Player;
import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.client.UnoClientManager;
import com.tobias.server.uno.command.Command;
import com.tobias.server.uno.command.CommandType;
import com.tobias.server.uno.command.CommandWorker;
import com.tobias.server.uno.handlers.ClientCommandHandler;
import com.tobias.server.uno.handlers.CommandHandler;
import com.tobias.server.uno.handlers.GameCommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UnoServer implements Runnable{

    private Map<String, CommandHandler> handlers;
    private UnoClientManager unoClientManager;
    private GameManager gameManager;
    private CommandWorker worker;
    private static final Logger LOGGER = LogManager.getLogger(UnoServer.class.getName());
    private boolean running;
    private boolean accepting;
    private int port;
    public static int maxPlayers;

    public UnoServer (int port){
        this.unoClientManager = new UnoClientManager();
        this.gameManager = new GameManager();
        this.handlers = new HashMap<>();
        this.port = port;
        this.handlers.put("CLIENT",new ClientCommandHandler(unoClientManager));
        this.handlers.put("GAME",new GameCommandHandler(gameManager,this.unoClientManager));
        this.worker = new CommandWorker(handlers);
        Thread t = new Thread(worker);
        t.setName("CommandWorker-UnoServer-" + t.getId());
        t.start();
    }

    public void run(){
        Thread.currentThread().setName("UnoServer-" + Thread.currentThread().getId());
        this.running = true;
        this.accepting = true;
        try(ServerSocket socket = new ServerSocket(this.port)){
            startPolling();
            LOGGER.info("Server started on port " + this.port + " with max players allowed: " + maxPlayers);
            while (running){
                if(accepting) {
                    UnoClient unoClient = new UnoClient(socket.accept(), getUnoClients().size(),new CommandWorker(handlers));
                    LOGGER.info("Client connected: " + unoClient.getIpAddress());
                    initiateClient(unoClient);
                }
                if(getUnoClients().size() == maxPlayers) {
                    accepting = false;
                    List<Player> players = new ArrayList<>();
                    List<Integer> playerIds = new ArrayList<>();
                    for(Player p : getPlayerFromClients()) {
                        if(!p.isInGame()) {
                            players.add(p);
                            playerIds.add(p.getId());
                        }
                    }
                    Game game = gameManager.newGame(players);
                    game.start();
                    initializeGame(game.getGameId(), playerIds);
                }
            }
        } catch (IOException e) {
            LOGGER.fatal("Server IO exception",e);
        }
    }
    public boolean isRunning(){
        return this.running;
    }
    private void initiateClient(UnoClient unoClient) {
        Thread clientThread = new Thread(unoClient);
        clientThread.setName("UnoClient-" + unoClient.getId());
        clientThread.start();
        unoClientManager.addClient(unoClient);
        handlers.get("CLIENT").process(new Command(CommandType.CLIENT_REGISTERID,Integer.toString(unoClient.getId())),unoClient);

    }

    private void initializeGame(int gameId, List<Integer> ids) {
        for (UnoClient client : getUnoClients()) {
            worker.(new Command(CommandType.GAME_START, Integer.toString(gameId)));
            handlers.get("PLAYER").process(new Command(CommandType.GAME_REGISTEROPPONENTPLAYER, String.join(',')));
            handlers.get("PLAYER").process(new Command(CommandType.GAME_DRAWCARD, "7"), c);
        }
    }
    private void startPolling() {
        ScheduledExecutorService ses;
        ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<UnoClient> clients = unoClientManager.checkForDisconnect();
                if(clients.size() > 0) {
                    for (UnoClient client : clients) {
                        // handlers.get("PLAYER").process(new Command(CommandType.PLAYER_DISCONNECT,Integer.toString(client.getId())),client);
                        handlers.get("CLIENT").process(new Command(CommandType.CLIENT_DISCONNECT,Integer.toString(client.getId())));
                    }
                }
            }
        },0,8, TimeUnit.SECONDS);
    }
    public void setAccepting(boolean val) {
        this.accepting = val;
    }
    public List<UnoClient> getUnoClients(){
        return unoClientManager.getClients();
    }
    private CommandHandler getHandlerAndProcess(Command c) {

    }
    private List<Player> getPlayerFromClients() {
        List<Player> players = new ArrayList<>();
        for (UnoClient c : getUnoClients()){
            players.add(c.getPlayer());
        }
        return players;
    }
}
