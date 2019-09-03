package com.tobias.server.uno.client;

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
        Thread.currentThread().setName("UnoClientManager-" + Thread.currentThread().getId());
    }

    public void addClient(UnoClient unoClient){
        if(unoClient != null){
            unoClients.add(unoClient);
        }
    }

    public void removeClient(UnoClient client) {
        unoClients.remove(client);
    }
    public int getNumberOfClients(){
        return unoClients.size();
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
}
