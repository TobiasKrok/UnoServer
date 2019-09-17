package com.tobias.server.uno.command;

import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.handlers.CommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandWorker implements Runnable{

    private List<CommandEvent> queue;
    private Map<String,CommandHandler> handlers;
    private static final Logger LOGGER = LogManager.getLogger(CommandWorker.class.getName());

    public CommandWorker(Map<String,CommandHandler> handlers ){
        this.queue = new LinkedList<>();
        this.handlers = handlers;
    }

    public void process(String command, UnoClient unoClient) {
        synchronized (queue) {
            queue.add(new CommandEvent(command, unoClient));
            queue.notify();
        }
    }

    public void process(Command command, UnoClient unoClient) {
        synchronized (queue) {
            queue.add(new CommandEvent(command,unoClient));
            queue.notify();
        }
    }

    public void process(Command command) {
        synchronized (queue) {
            queue.add(new CommandEvent(command));
            queue.notify();
        }
    }
    public void run(){
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()){
                    try {
                        queue.wait();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LOGGER.warn("Worker interrupted, perhaps the client disconnected?",e);
                    }
                }
            }
            Command c;
            if(queue.get(0).getCommand() instanceof String) {
                c = parseCommand((String)queue.get(0).getCommand());
            } else {
                c = (Command) queue.get(0).getCommand();
            }

            LOGGER.debug("Command received from Client " +  queue.get(0).getUnoClient().getId() + ": " + c.toString());
            if(!(c.getType() == CommandType.WORKER_UNKNOWNCOMMAND)) {
                if(queue.get(0).getUnoClient() == null)
                getHandlerForCommand(c).process(c,queue.get(0).getUnoClient());
            }
            queue.remove(0);
        }
    }

    private Command parseCommand(String command) {
        System.out.println(command);
        // Set the commandType to an unknown commandType. If we can't convert the passed commandType to a CommandType, WORKER_UNKNOWNCOMMAND will be returned.
        CommandType cmdType = CommandType.WORKER_UNKNOWNCOMMAND;
        String data = "";
        try {
           cmdType = CommandType.valueOf(command.substring(command.indexOf("TYPE:") + 5,command.indexOf("DATA:") - 1));
           data = command.substring(command.indexOf("DATA:") + 5);
        } catch (IllegalArgumentException e){
            LOGGER.error("Could not bind Type: " + (command.substring(command.indexOf("TYPE:") + 5,command.indexOf("DATA:"))) + " to a CommandType");
        }
        return new Command(cmdType,data);
    }

    private CommandHandler getHandlerForCommand(Command command){
        String prefix = command.getType().name().substring(0,command.getType().name().indexOf("_"));
        return handlers.get(prefix);
    }
}
