package com.conorsmine.net.banbt;

import com.conorsmine.net.banbt.cmds.BaNBTCmdManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BaNBT extends JavaPlugin {

    private ConfigFile configFile;

    @Override
    public void onEnable() {
        configFile = new ConfigFile(this);

        getCommand("banbt").setExecutor(new BaNBTCmdManager(this));
        getCommand("banbt").setTabCompleter(new BaNBTCmdManager(this));
        log("§aSuccessfully enabled!");
    }

    @Override
    public void onDisable() {

    }

    public void log(String... msg) {
        for (String s : msg) {
            getLogger().info(configFile.getPrefix() + s);
        }
    }

    public ConfigFile getCfgFile() {
        return configFile;
    }
}
