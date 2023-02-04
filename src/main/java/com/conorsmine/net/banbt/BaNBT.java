package com.conorsmine.net.banbt;

import fr.andross.banitem.BanItemAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class BaNBT extends JavaPlugin {

    @Override
    public void onEnable() {
        final BanItemAPI banItemApi = BanItemAPI.getInstance();
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
