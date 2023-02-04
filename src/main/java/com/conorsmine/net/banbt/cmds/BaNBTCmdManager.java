package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import com.conorsmine.net.banbt.ConfigFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class BaNBTCmdManager implements TabExecutor {

    private final BaNBT pl;
    private final ConfigFile cfg;

    public BaNBTCmdManager(BaNBT pl) {
        this.pl = pl;
        this.cfg = pl.getCfgFile();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { ; return false; }

        switch (args[0]) {

        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    private void sendCmdUsageMsg(CommandSender sender) {
        sender.sendMessage(cfg.getPrefix() + "§c");
    }
}
