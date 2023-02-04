package com.conorsmine.net.banbt;

import org.bukkit.plugin.java.JavaPlugin;

public final class BaNBT extends JavaPlugin {

    private ConfigFile configFile;

    @Override
    public void onEnable() {
        configFile = new ConfigFile(this);

        getCommand("banbt").setExecutor(new BaNBTCmd(this));
        getCommand("banbt").setTabCompleter(new BaNBTCmd(this));
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
