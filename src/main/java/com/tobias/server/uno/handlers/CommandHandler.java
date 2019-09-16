package com.tobias.server.uno.handlers;

import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.command.Command;

public interface CommandHandler {

     void process(Command command, UnoClient unoClient);
     void process(Command command);

}
