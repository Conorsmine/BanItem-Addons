package com.conorsmine.net.banbt.autoBan.filter;

import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class ConsoleMessageFilter implements MessageFilter {

    private final List<String> msgCache = new LinkedList<>();
    private final Logger logger;
    private boolean stopLogging = false;
    private boolean discard = false;

    public ConsoleMessageFilter(Logger logger) {
        this.logger = logger;
    }

    // Todo:
    //  This does not work yet
    //  And I really do not know why!
    //  It's the same algorithm as in the chat
    //  Same inputs, but it just does not work
    //  Hours spent: 1.5
    //  Lets see how this goes
    @Override
    public boolean sendMessage(String msg) {
        String plain = msg.replaceAll("§.", "");
        boolean isSep = plain.equals(SEP);

        if (plain.equals(DIS)) discard = true;
        if (isSep) stopLogging = !stopLogging;
        if (isSep && !stopLogging) return onBlockComplete();

        if (stopLogging) msgCache.add(msg + ID);


        return !stopLogging;
    }

    private boolean onBlockComplete() {
        if (discard) {
            msgCache.clear();
            stopLogging = false;
            discard = false;
            return false;
        }
        else {
            sendCache();
            msgCache.clear();
            stopLogging = false;
            discard = false;
            return true;
        }
    }

    private void sendCache() {
        msgCache.forEach(logger::info);
    }
}
