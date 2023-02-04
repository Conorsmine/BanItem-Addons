package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class AbstractBanNBTCmd {

    private final BaNBT plugin;
    private final BaNBTCmdManager cmdManager;

    public AbstractBanNBTCmd(BaNBT plugin, BaNBTCmdManager cmdManager) {
        this.plugin = plugin;
        this.cmdManager = cmdManager;
    }

    abstract void execute(CommandSender sender, String[] args);

    abstract List<String> tabcomplete(CommandSender sender, String[] args);

    public BaNBT getPlugin() {
        return plugin;
    }

    public BaNBTCmdManager getCmdManager() {
        return cmdManager;
    }
}
