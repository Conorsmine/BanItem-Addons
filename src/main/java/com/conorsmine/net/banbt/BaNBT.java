package com.conorsmine.net.banbt;

import com.conorsmine.net.banbt.cmds.BaNBTCmdManager;
import com.conorsmine.net.banbt.files.ConfigFile;
import com.conorsmine.net.banbt.files.LogFile;
import fr.andross.banitem.BanItem;
import fr.andross.banitem.BanItemAPI;
import fr.andross.banitem.actions.BanAction;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class BaNBT extends JavaPlugin {

    private ConfigFile configFile;
    private LogFile logFile;
    private BanItemAPI banItemAPI;
    private boolean log = false;

    @Override
    public void onEnable() {
        configFile = new ConfigFile(this);
        configFile.initData();
        logFile = new LogFile(this);
        banItemAPI = BanItemAPI.getInstance();

        getCommand("banbt").setExecutor(new BaNBTCmdManager(this));
        getCommand("banbt").setTabCompleter(new BaNBTCmdManager(this));
        log = checkLogging();
        configFile.initData();

        log("§aSuccessfully enabled!");
    }

    @Override
    public void onDisable() {
    }

    private boolean checkLogging() {
        if (configFile.isLogging()) {
            FileConfiguration banItemConfig = BanItem.getInstance().getConfig();
            if (!banItemConfig.getConfigurationSection("api").getBoolean("playerbanitemevent")) {
                log("§cPlease enable the §6\"playerbanitemeven\"§c in the BanItem config!",
                        "Otherwise the plugin will not be able to log violations.");
                return false;
            }
            else {
                getServer().getPluginManager().registerEvents(new EventListener(this), this);
                return true;
            }
        }
        return false;
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

    // This boolean is absolute
    public boolean isLog() {
        return log;
    }
}
