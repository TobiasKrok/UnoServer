package com.tobias.server.uno.command;

import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.handlers.AbstractCommandHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandWorker implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(CommandWorker.class.getName());
    private List<Object> queue;
    private Map<String, AbstractCommandHandler> handlers;

    public CommandWorker(Map<String, AbstractCommandHandler> handlers) {
        this.queue = new LinkedList<>();
        this.handlers = handlers;
    }

    public void process(String command, UnoClient unoClient) {
        synchronized (queue) {
            queue.add(new CommandEvent(parseCommand(command), unoClient));
            queue.notify();
        }
    }

    public void process(Command command, UnoClient unoClient) {
        synchronized (queue) {
            queue.add(new CommandEvent(command, unoClient));
            queue.notify();
        }
    }

    public void process(Command command) {
        synchronized (queue) {
            queue.add(command);
            queue.notify();
        }
    }

    public void run() {
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LOGGER.warn("Worker interrupted, perhaps the client disconnected?", e);
                    }
                }
            }

            if (queue.get(0) instanceof CommandEvent) {
                CommandEvent event = (CommandEvent) queue.get(0);
                LOGGER.debug("Command received from Client " + event.getUnoClient().getId() + ": " + event.getCommand().toString());
                if (!(event.getCommand().getType() == CommandType.WORKER_UNKNOWNCOMMAND)) {
                    try {
                        getHandlerForCommand(event.getCommand()).process(event.getCommand(), event.getUnoClient());
                    } catch (NullPointerException e) {
                        LOGGER.error("No handler could process this command: " + queue.get(0).toString(), e);
                    }

                }
            } else if (queue.get(0) instanceof Command) {
                LOGGER.debug("Command received from Server : " + queue.get(0).toString());
                if (!(((Command) queue.get(0)).getType() == CommandType.WORKER_UNKNOWNCOMMAND)) {
                    try {
                        getHandlerForCommand((Command) queue.get(0)).process((Command) queue.get(0));
                    } catch (NullPointerException e) {
                        LOGGER.error("No handler could process this command: " + queue.get(0).toString(), e);
                    }
                }
            } else {
                LOGGER.error("Could not resolve object in queue!" + queue.get(0).toString());
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
            cmdType = CommandType.valueOf(command.substring(command.indexOf("TYPE:") + 5, command.indexOf("DATA:") - 1));
            data = command.substring(command.indexOf("DATA:") + 5);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Could not bind Type: " + (command.substring(command.indexOf("TYPE:") + 5, command.indexOf("DATA:"))) + " to a CommandType");
        }
        return new Command(cmdType, data);
    }

    private AbstractCommandHandler getHandlerForCommand(Command command) {
        String prefix = command.getType().name().substring(0, command.getType().name().indexOf("_"));
        return handlers.get(prefix);
    }
}
