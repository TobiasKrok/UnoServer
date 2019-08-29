package com.tobias.server.uno;

import com.tobias.game.Game;
import com.tobias.game.Player;
import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.client.UnoClientManager;
import com.tobias.server.uno.command.Command;
import com.tobias.server.uno.command.CommandType;
import com.tobias.server.uno.command.CommandWorker;
import com.tobias.server.uno.handlers.ClientCommandHandler;
import com.tobias.server.uno.handlers.CommandHandler;
import com.tobias.server.uno.handlers.GameCommandHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnoServer implements Runnable{
    private Map<String, CommandHandler> handlers;
    private UnoClientManager unoClientManager;
    private boolean running;
    private boolean accepting;
    private Game game;
    private int port;

    public UnoServer (int port){
        this.unoClientManager = new UnoClientManager();
        this.handlers = new HashMap<>();
        this.port = port;
        this.handlers.put("CLIENT",new ClientCommandHandler());
    }

    public void run(){
        this.running = true;
        this.accepting = true;
        List<UnoClient> disconnectedClients;
        try(ServerSocket socket = new ServerSocket(this.port)){
            System.out.println("[NET] - Server started on port " + this.port + " and address " + socket.getInetAddress());
            while (running){
                if(accepting) {
                    UnoClient unoClient = new UnoClient(socket.accept(), getUnoClients().size(),new CommandWorker(handlers));
                    System.out.println("[NET] - Connection from: " + unoClient.getIpAddress());
                    new Thread(unoClient).start();
                    unoClientManager.addClient(unoClient);
                    unoClientManager.sendToClient(unoClient,new Command(CommandType.CLIENT_REGISTERID,Integer.toString(unoClient.getId())));
                    if(getUnoClients().size() == 2) {
                        accepting = false;
                    }
                }
                disconnectedClients = unoClientManager.checkForDisconnect();
                if(disconnectedClients.size() > 0) {
                    for (UnoClient client : getUnoClients()) {
                        handlers.get("PLAYER").process(new Command(CommandType.PLAYER_DISCONNECT,Integer.toString(client.getId())),client);
                        handlers.get("CLIENT").process(new Command(CommandType.CLIENT_DISCONNECT,Integer.toString(client.getId())),client);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[NET - ERROR}: " + e.getMessage());
        }
    }
    public boolean isRunning(){
        return this.running;
    }

    public void setGameInstance(Game game){
        this.game = game;
        // We create a new GameCommandHandler every time a new game starts.
        handlers.put("PLAYER",new GameCommandHandler(game.getGameManager(),this.unoClientManager));
    }

    public void initializeGame() {
        for (UnoClient c : getUnoClients()) {
            handlers.get("PLAYER").process(new Command(CommandType.PLAYER_DRAWCARD,"7"), c);
            handlers.get("CLIENT").process(new Command(CommandType.CLIENT_REGISTEROPPONENTPLAYER,String.valueOf(getUnoClients().size() - 1)), c);
            handlers.get("CLIENT").process(new Command(CommandType.CLIENT_GAMESTART,""), c);
        }
    }

    public void setAccepting(boolean val) {
        this.accepting = val;
    }
    public List<UnoClient> getUnoClients(){
        return unoClientManager.getClients();
    }

    public List<Player> getPlayersFromClients() {
        List<Player> players = new ArrayList<>();
        for (UnoClient c : getUnoClients()){
            players.add(c.getPlayer());
        }
        return players;
    }
}
