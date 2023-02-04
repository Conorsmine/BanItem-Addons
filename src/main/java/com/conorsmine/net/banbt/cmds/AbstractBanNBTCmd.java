package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class AbstractBanNBTCmd {

    private final String cmdName;
    private final String description;
    private final BaNBT plugin;
    private final BaNBTCmdManager cmdManager;
    private boolean needsPlayer = false;

    public AbstractBanNBTCmd(String cmdName, String description, BaNBT plugin, BaNBTCmdManager cmdManager) {
        this.cmdName = cmdName;
        this.description = description;
        this.plugin = plugin;
        this.cmdManager = cmdManager;
    }

    abstract void execute(CommandSender sender, String[] args);

    abstract List<String> tabcomplete(CommandSender sender, String[] args);

    public String getHeader() {
        return "§7§m     §r §l[§6§l" + cmdName + "§r§l] §7§m     ";
    }

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

    public boolean isNeedsPlayer() {
        return needsPlayer;
    }

    public void setNeedsPlayer(boolean needsPlayer) {
        this.needsPlayer = needsPlayer;
    }
}
