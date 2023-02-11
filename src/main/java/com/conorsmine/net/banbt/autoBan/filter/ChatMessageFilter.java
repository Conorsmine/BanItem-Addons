package com.conorsmine.net.banbt.autoBan.filter;

import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class ChatMessageFilter implements MessageFilter {

    private final List<String> msgCache = new CopyOnWriteArrayList<>();
    private final Player p;
    private boolean stopLogging = false;
    private boolean discard = false;

    public ChatMessageFilter(Player p) {
        this.p = p;
    }

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
            for (String s : msgCache) p.sendMessage(s);
            msgCache.clear();
            stopLogging = false;
            discard = false;
            return true;
        }
    }
}
