package com.tobias.server.uno.client;

import com.tobias.uno.Player;
import com.tobias.server.uno.command.Command;
import com.tobias.server.uno.command.CommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnoClientManager {
   private List<UnoClient> unoClients;
   private static final Logger LOGGER = LogManager.getLogger(UnoClientManager.class.getName());


    public UnoClientManager() {
        this.unoClients = new ArrayList<>();

    }

    public void addClient(UnoClient unoClient){
        if(unoClient != null){
            unoClients.add(unoClient);
        }
    }

    public void sendToClient(UnoClient unoClient, Command command) {
        try {
            unoClient.write(command.toString());
            LOGGER.debug("Command sent to Client " + unoClient.getId() + ": " + command.toString());
        } catch (IOException e){
            LOGGER.error("Failed to write to Client " + unoClient.getId(),e);
            unoClient.close();
        }
    }

    public List<UnoClient> checkForDisconnect() {
        List<UnoClient> disc = new ArrayList<>();
        Iterator<UnoClient> iter = unoClients.iterator();
        while(iter.hasNext()){
            UnoClient c = iter.next();
            sendToClient(c, new Command(CommandType.CLIENT_POLL, ""));
            if (c.isDisconnected()) {
                disc.add(c);
                iter.remove();
            }
        }
        return disc;
    }
    public List<UnoClient> getClients() {
        return this.unoClients;
    }

    public void sendToAllClients(Command command){
        for(UnoClient unoClient : unoClients){
           sendToClient(unoClient,command);
        }
    }

    public List<Integer> getPlayerIds() {
        List<Integer> ids = new ArrayList<>();
        for (Player p : getPlayerFromClients()) {
            ids.add(p.getId());
        }
        return ids;
    }
    public List<Player> getPlayerFromClients() {
        List<Player> players = new ArrayList<>();
        for (UnoClient c : unoClients){
            players.add(c.getPlayer());
        }
        return players;
    }

    public boolean clientsAreInGame() {
        for (Player p : getPlayerFromClients()) {
            if(p.isInGame()) {
                return true;
            }
        }
        return false;
    }
}
