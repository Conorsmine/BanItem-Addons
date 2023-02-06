package com.conorsmine.net.banbt;

import com.conorsmine.net.banbt.cmds.BaNBTCmdManager;
import com.conorsmine.net.banbt.files.ConfigFile;
import com.conorsmine.net.banbt.files.LogFile;
import fr.andross.banitem.BanItemAPI;
import fr.andross.banitem.actions.BanAction;
import org.bukkit.plugin.java.JavaPlugin;

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
        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        log("§aLogging the following actions:");
        for (BanAction logAction: configFile.getLogActions()) {
            log(String.format(" -%s", logAction.getName()));
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
