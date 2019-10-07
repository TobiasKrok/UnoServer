package com.tobias;

import com.tobias.server.uno.UnoServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());
    public static void main(String[] args) {

        UnoServer unoServer = new UnoServer(Integer.parseInt(args[0]));
        UnoServer.minPlayers = Integer.parseInt(args[1]);
        new Thread(unoServer).start();
        }
}
