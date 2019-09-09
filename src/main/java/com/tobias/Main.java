package com.tobias;

import com.tobias.server.uno.UnoServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());
    public static void main(String[] args) {

        UnoServer unoServer = new UnoServer(Integer.parseInt(args[0]));
        UnoServer.maxPlayers = Integer.parseInt(args[1]);
        new Thread(unoServer).start();

/*            Game game = new Game();
            List<Player> players = new ArrayList<>();
            for (UnoClient c : unoServer.getUnoClients()) {
                players.add(c.getPlayer());
            }
            try {
                game.setPlayers(players);
            } catch (InvalidParameterException e) {

            }

            unoServer.setGameInstance(game);*/
        }
}
