package com.conorsmine.net.banbt;

import fr.andross.banitem.events.PlayerBanItemEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;

public class EventListener implements Listener {

    private final BaNBT pl;

    public EventListener(BaNBT pl) {
        this.pl = pl;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerBanItem(PlayerBanItemEvent event) {
        if (event.getType() != PlayerBanItemEvent.Type.BLACKLIST) return;
        if (Arrays.stream(pl.getCfgFile().getLogActions())
                .noneMatch(banAction -> banAction == event.getAction())) return;

        String logReason = String.format("BanAction: %s", event.getAction().getName());
        pl.getLogFile().addLog(event.getPlayer(), event.getBannedItem().getItemStack(), logReason);
    }
}
