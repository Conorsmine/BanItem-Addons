package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class AbstractBanNBTCmd {

    private final String cmdName;
    private final String description;
    private final BaNBT plugin;
    private final BaNBTCmdManager cmdManager;

    public AbstractBanNBTCmd(String cmdName, String description, BaNBT plugin, BaNBTCmdManager cmdManager) {
        this.cmdName = cmdName;
        this.description = description;
        this.plugin = plugin;
        this.cmdManager = cmdManager;
    }

    abstract void execute(CommandSender sender, String[] args);

    abstract List<String> tabcomplete(CommandSender sender, String[] args);

    public String getCmdName() {
        return cmdName;
    }

    public String getDescription() {
        return description;
    }

    public BaNBT getPlugin() {
        return plugin;
    }

    public BaNBTCmdManager getCmdManager() {
        return cmdManager;
    }
}
