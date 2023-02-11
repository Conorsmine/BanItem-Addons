package com.conorsmine.net.banbt;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.conorsmine.net.banbt.autoBan.AutoBanManager;
import com.conorsmine.net.banbt.autoBan.filter.BanChatFilter;
import com.conorsmine.net.banbt.autoBan.filter.BanConsoleFilter;
import com.conorsmine.net.banbt.cmds.BaNBTCmdManager;
import com.conorsmine.net.banbt.files.BanFile;
import com.conorsmine.net.banbt.files.ConfigFile;
import com.conorsmine.net.banbt.files.LogFile;
import fr.andross.banitem.BanItem;
import fr.andross.banitem.BanItemAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class BaNBT extends JavaPlugin {

    private ConfigFile configFile;
    private LogFile logFile;
    private BanFile banFile;

    private BanItemAPI banItemAPI;
    private AutoBanManager banManager;
    private boolean log = false;

    @Override
    public void onEnable() {
        configFile = new ConfigFile(this);
        banManager = new AutoBanManager(this);
        banManager.reloadBannableItemsFromConfig();
        configFile.initData();
        logFile = new LogFile(this);
        banFile = new BanFile(this);
        banItemAPI = BanItemAPI.getInstance();

        if (isProtocolInstalled()) initMessageFilters();
        getCommand("banbt").setExecutor(new BaNBTCmdManager(this));
        getCommand("banbt").setTabCompleter(new BaNBTCmdManager(this));

        log = checkLogging();
        configFile.initData();

        log("§aSuccessfully enabled!");
    }

    @Override
    public void onDisable() {
    }

    private void initMessageFilters() {
        // Console filter
        ((Logger) LogManager.getRootLogger()).addFilter(new BanConsoleFilter((Logger) LogManager.getRootLogger()));

        // Player chat filter
        BanChatFilter chatFilter = new BanChatFilter(this);
        getServer().getPluginManager().registerEvents(chatFilter, this);
        ProtocolLibrary.getProtocolManager().addPacketListener(chatFilter);
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

    private boolean isProtocolInstalled() {
        return getServer().getPluginManager().getPlugin("ProtocolLib") != null;
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

    public BanFile getBanFile() {
        return banFile;
    }

    public BanItemAPI getBanItemAPI() {
        return banItemAPI;
    }

    public AutoBanManager getBanManager() {
        return banManager;
    }

    // This boolean is absolute
    public boolean isLog() {
        return log;
    }
}
