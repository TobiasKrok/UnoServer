package com.tobias.server.uno.command;

import com.tobias.server.uno.client.UnoClient;
import com.tobias.server.uno.handlers.CommandHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandWorker implements Runnable{

    private List<CommandEvent> queue;
    private Map<String,CommandHandler> handlers;

    public CommandWorker(Map<String,CommandHandler> handlers ){
        this.queue = new LinkedList<>();
        this.handlers = handlers;
    }

    public  void processCommand(String command, UnoClient unoClient) {
        synchronized (queue) {
            queue.add(new CommandEvent(command, unoClient));
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
                        e.printStackTrace();
                    }
                }
            }
            Command c = parseCommand(queue.get(0).getStringCommand());
            if(!(c.getType() == CommandType.WORKER_UNKNOWNCOMMAND)) {
                getHandlerForCommand(c).process(c,queue.get(0).getUnoClient());
            }
            queue.remove(0);
        }
    }

    private Command parseCommand(String command) {
        int id = Character.getNumericValue(command.charAt(3));
        // Set the commandType to an unknown commandType. If we can't convert the passed commandType to a CommandType, WORKER_UNKNOWNCOMMAND will be returned.
        CommandType cmdType = CommandType.WORKER_UNKNOWNCOMMAND;
        String data = "";
        try {
           cmdType = CommandType.valueOf(command.substring(command.indexOf("TYPE:") + 5,command.indexOf("DATA:") - 1));
           data = command.substring(command.indexOf("DATA:") + 5);
        } catch (IllegalArgumentException e){
            System.out.println("Could not bind Type: " + (command.substring(command.indexOf("TYPE:") + 5,command.indexOf("DATA:"))) + " to a CommandType");
        }
        return new Command(cmdType,data);
    }

    private CommandHandler getHandlerForCommand(Command command){
        String prefix = command.getType().name().substring(0,command.getType().name().indexOf("_"));
        return handlers.get(prefix);
    }
}
