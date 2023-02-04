package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCmd extends AbstractBanNBTCmd{

    public ReloadCmd(String cmdName, BaNBT plugin, BaNBTCmdManager cmdManager) {
        super(cmdName, "Reload the plugin. Primarily the config", plugin, cmdManager);
    }

    @Override
    void execute(CommandSender sender, String[] args) {

    }

    @Override
    List<String> tabcomplete(CommandSender sender, String[] args) {
        return null;
    }
}
