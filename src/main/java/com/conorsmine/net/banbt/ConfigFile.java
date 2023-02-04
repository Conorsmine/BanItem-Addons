package com.conorsmine.net.banbt;

import org.bukkit.configuration.file.FileConfiguration;

@SuppressWarnings("unchecked")
public class ConfigFile {

    private final BaNBT pl;
    private final FileConfiguration config;
    private String prefix = "§c§l[§e§lBaNBT§c§l] ";

    public ConfigFile(BaNBT pl) {
        this.pl = pl;
        pl.saveDefaultConfig();
        config = pl.getConfig();

        initData();
    }

    private void initData() {
        prefix = getOrDefault("prefix", prefix);
    }

    public void reload() {
        pl.reloadConfig();
        initData();
    }

    private <E> E getOrDefault(String path, E other) {
        return (E) config.get(path, other);
    }

    public String getPrefix() {
        return prefix;
    }
}
