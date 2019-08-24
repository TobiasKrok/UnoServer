package com.tobias.server.uno.client;

import java.util.ArrayList;
import java.util.List;

public class
UnoClientManager {
   private List<UnoClient> unoClients;


    public UnoClientManager() {
        this.unoClients = new ArrayList<>();
    }

    public void addClient(UnoClient unoClient){
        if(unoClient != null){
            unoClients.add(unoClient);
        }
    }
    public int getNumberOfClients(){
        return unoClients.size();
    }

    public void sendToClient(UnoClient unoClient, String command){
        unoClient.write(command);
    }
    public void sendToAllClients(String command){
        for(UnoClient unoClient : unoClients){
            unoClient.write(command);
        }
    }
}
