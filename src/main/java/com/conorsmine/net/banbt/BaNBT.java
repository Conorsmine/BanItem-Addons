package com.conorsmine.net.banbt;

import com.conorsmine.net.banbt.cmds.BaNBTCmdManager;
import com.conorsmine.net.banbt.files.ConfigFile;
import com.conorsmine.net.banbt.files.LogFile;
import fr.andross.banitem.BanItemAPI;
import fr.andross.banitem.actions.BanAction;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class BaNBT extends JavaPlugin {

    private ConfigFile configFile;
    private LogFile logFile;
    private BanItemAPI banItemAPI;

    @Override
    public void onEnable() {
        configFile = new ConfigFile(this);
        configFile.initData();
        logFile = new LogFile(this);
        banItemAPI = BanItemAPI.getInstance();

        getCommand("banbt").setExecutor(new BaNBTCmdManager(this));
        getCommand("banbt").setTabCompleter(new BaNBTCmdManager(this));

        if (configFile.isLogging()) {
            FileConfiguration banItemConfig = getServer().getPluginManager().getPlugin("BanItem").getConfig();
            if (!banItemConfig.getConfigurationSection("api").getBoolean("playerbanitemevent")) {
                log("§cPlease enable the §6\"playerbanitemeven\"§c in the BanItem config!\n" +
                        "§cOtherwise the plugin will not be able to log violations.");
            }
            else {
                getServer().getPluginManager().registerEvents(new EventListener(this), this);
            }
        }

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

    public LogFile getLogFile() {
        return logFile;
    }

    public BanItemAPI getBanItemAPI() {
        return banItemAPI;
    }
}
