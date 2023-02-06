package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCmd extends AbstractBanNBTCmd{

    public ReloadCmd(String cmdName, BaNBT plugin, BaNBTCmdManager cmdManager) {
        super(cmdName, "Reload the plugin. Primarily the config", plugin, cmdManager);
    }

    @Override
    void execute(CommandSender sender, String[] args) {
        getPlugin().getCfgFile().reload();
        getPlugin().getLogFile().reload();
        sender.sendMessage(getPlugin().getCfgFile().getPrefix() + getHeader());
        sender.sendMessage(getPlugin().getCfgFile().getPrefix() + "§aReloaded plugin.");

        if (!(sender instanceof Player)) return;
        Player p = ((Player) sender);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.2f);
    }

    @Override
    List<String> tabcomplete(CommandSender sender, String[] args) {
        return null;
    }
}
