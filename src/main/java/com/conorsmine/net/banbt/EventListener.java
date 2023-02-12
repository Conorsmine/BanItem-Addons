package com.conorsmine.net.banbt;

import com.conorsmine.net.banbt.autoBan.AutoBanManager;
import fr.andross.banitem.events.PlayerBanItemEvent;
import fr.andross.banitem.items.BannedItem;
import fr.andross.banitem.items.CustomBannedItem;
import fr.andross.banitem.utils.DoubleMap;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Stream;

public class EventListener implements Listener {

    private final BaNBT pl;

    public EventListener(BaNBT pl) {
        this.pl = pl;
    }

    @EventHandler()
    public void onPlayerBanItem(PlayerBanItemEvent event) {
        if (event.getType() != PlayerBanItemEvent.Type.BLACKLIST) return;
        if (Arrays.stream(pl.getCfgFile().getLogActions())
                .noneMatch(banAction -> banAction == event.getAction())) return;

        Player p = event.getPlayer();
        String logReason = String.format("BanAction: %s", event.getAction().getName());
        pl.getLogFile().addLog(p, event.getBannedItem().getItemStack(), logReason);

        if (isBannable(event))
            banPlayer(p, event);
    }

    private void banPlayer(Player p, PlayerBanItemEvent event) {
        String plPrefix = pl.getCfgFile().getPrefix();
        Bukkit.getBanList(BanList.Type.NAME)
                .addBan(p.getName(), String.format("%sIllegal item", plPrefix), expireNever(), plPrefix);
        pl.getBanFile().addBan(p, event.getBannedItem().getItemStack());
        pl.log(String.format("§cBanned §b%s §cfor carrying a banned item!", p.getName()));
        p.kickPlayer(String.format("%sIllegal item", plPrefix));
    }

    private boolean isBannable(final PlayerBanItemEvent event) {
        ItemStack bannedItem = event.getBannedItem().getItemStack();
        AutoBanManager manager = pl.getBanManager();
        if (bannedItem == null) return false;

        if (manager.contains(bannedItem.getType().name().toLowerCase(Locale.ROOT))) return true;
        return pl.getBanItemAPI().getCustomItems().getReversed().keySet().stream()
                .anyMatch(custom ->
                        custom.matches(bannedItem) &&
                        manager.contains(custom.getName().toLowerCase(Locale.ROOT))
                );
    }

    private Date expireNever() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, Integer.MAX_VALUE);
        return calendar.getTime();
    }
}
