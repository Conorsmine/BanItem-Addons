package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import fr.andross.banitem.BanItem;
import fr.andross.banitem.actions.BanAction;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ReloadCmd extends AbstractBanNBTCmd{

    public ReloadCmd(String cmdName, BaNBT plugin, BaNBTCmdManager cmdManager) {
        super(cmdName, "Reload the plugin. Primarily the config", plugin, cmdManager);
    }

    @Override
    void execute(CommandSender sender, String[] args) {
        getPlugin().getCfgFile().reload();
        getPlugin().getLogFile().reload();
        getPlugin().getBanFile().reload();
        getPlugin().initMessageFilters();

        String prefix = getPlugin().getCfgFile().getPrefix();
        sender.sendMessage(prefix + getHeader());

        getPlugin().printLogActions(sender);
        getPlugin().printBannableItems(sender);

        sender.sendMessage(prefix + "§7Use §b/bi reload §7first, if changes");
        sender.sendMessage(prefix + "§7occurred in the BanItem §3config.yml §7file");
        sender.sendMessage(prefix + "§aReloaded plugin.");

        if (!(sender instanceof Player)) return;
        Player p = ((Player) sender);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.2f);
    }

    @Override
    List<String> tabcomplete(CommandSender sender, String[] args) {
        return null;
    }
}
