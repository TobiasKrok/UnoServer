package com.tobias.server.uno.handlers;

import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.command.Command;
import com.tobias.server.uno.command.CommandWorker;

public abstract class AbstractCommandHandler {

    CommandWorker worker;

    public void setWorker(CommandWorker worker) {
       this.worker = worker;
   }

    public abstract void process(Command command, UnoClient unoClient);

    public abstract void process(Command command);

}
