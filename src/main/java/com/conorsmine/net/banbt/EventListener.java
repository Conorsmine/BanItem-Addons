package com.conorsmine.net.banbt;

import com.conorsmine.net.banbt.autoBan.AutoBanManager;
import fr.andross.banitem.BanItem;
import fr.andross.banitem.events.PlayerBanItemEvent;
import fr.andross.banitem.items.BannedItem;
import fr.andross.banitem.items.CustomBannedItem;
import fr.andross.banitem.items.MetaItem;
import fr.andross.banitem.utils.DoubleMap;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

        if (pl.getCfgFile().isBannable() && isBannable(event)) {
            p.getInventory().remove(Objects.requireNonNull(event.getBannedItem().getItemStack()));

            banPlayer(p, event);
        }
    }

    private void banPlayer(Player p, PlayerBanItemEvent event) {
        String plPrefix = pl.getCfgFile().getPrefix();
        Bukkit.getBanList(BanList.Type.NAME)
                .addBan(p.getName(), String.format("%sIllegal item", plPrefix), null, plPrefix);
        pl.getBanFile().addBan(p, event.getBannedItem().getItemStack());
        pl.log(String.format("§cBanned §b%s §cfor carrying a banned item!", p.getName()));
        p.kickPlayer(String.format("%sIllegal item", plPrefix));
    }

    private boolean isBannable(final PlayerBanItemEvent event) {
        ItemStack bannedItem = event.getBannedItem().getItemStack();
        AutoBanManager manager = pl.getBanManager();
        if (bannedItem == null) return false;

        if (isRegularBannable(bannedItem, manager)) return true;
        if (isMetaBannable(event.getBannedItem(), manager)) return true;
        return isCustomBannable(bannedItem, manager);
    }

    private boolean isMetaBannable(BannedItem bannedItem, AutoBanManager manager) {
        return pl.getBanItemAPI().getDatabase().getMetaItems().getReversed().keySet().stream()
                .anyMatch(meta ->
                        meta.equals(bannedItem) &&
                                manager.contains(((MetaItem) meta).getName())
                );
    }

    private boolean isCustomBannable(ItemStack bannedItem, AutoBanManager manager) {
        return pl.getBanItemAPI().getCustomItems().getReversed().keySet().stream()
                .anyMatch(custom ->
                        custom.matches(bannedItem) &&
                                manager.contains(custom.getName().toLowerCase(Locale.ROOT))
                );
    }

    private boolean isRegularBannable(ItemStack bannedItem, AutoBanManager manager) {
        return manager.contains(bannedItem.getType().name().toLowerCase(Locale.ROOT));
    }
}
