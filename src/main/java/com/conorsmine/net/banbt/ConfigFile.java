package com.conorsmine.net.banbt;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

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

    private String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private <E> E getOrDefault(String path, E other) {
        final Object o = config.get(path, other);

        if (o instanceof String) return (E) c((String) o);
        else if (o instanceof List && ((List<E>) config.get(path, other)).set(0, null) instanceof String) {
            List<String> strList = new ArrayList<>();
            ((List<String>) o).forEach(s -> strList.add(c(s)));
            return (E) strList;
        }


        return (E) config.get(path, other);
    }

    public String getPrefix() {
        return prefix;
    }
}
