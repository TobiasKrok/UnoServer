package com.tobias.server.uno.handlers;

import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.command.Command;

public abstract class AbstractCommandHandler {

   public abstract void process(Command command, UnoClient unoClient);

   public abstract void process(Command command);
}
