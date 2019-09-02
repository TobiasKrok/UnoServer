package com.tobias;

import com.tobias.game.Game;
import com.tobias.server.uno.UnoServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());
    public static void main(String[] args) {
        UnoServer unoServer = new UnoServer(5000);
        new Thread(unoServer).start();
        while (unoServer.isRunning()) {
            if(unoServer.getUnoClients().size() == 4){
                unoServer.setAccepting(false);
                Game game = new Game();
                game.setPlayers(unoServer.getPlayersFromClients());
            }
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
}
