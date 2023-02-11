package com.conorsmine.net.banbt.autoBan;

import com.conorsmine.net.banbt.BaNBT;
import fr.andross.banitem.BanItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AutoBanManager extends HashSet<String> {

    private final BaNBT pl;

    public AutoBanManager(BaNBT pl) {
        this.pl = pl;
    }

    public void reloadBannableItemsFromConfig() {
        clear();
        FileConfiguration config = BanItem.getInstance().getConfig();

        if (!config.contains("blacklist")) return;
        ConfigurationSection blacklist = config.getConfigurationSection("blacklist");

        for (String world : blacklist.getKeys(false)) {
            ConfigurationSection worldSection = blacklist.getConfigurationSection(world);

            for (String item : worldSection.getKeys(false)) {
                ConfigurationSection itemSection = worldSection.getConfigurationSection(item);

                if (itemIsBannable(itemSection)) add(item.toLowerCase(Locale.ROOT));
            }
        }
    }

    private boolean itemIsBannable(ConfigurationSection itemSection) {
        for (String action : itemSection.getKeys(false)) {
            ConfigurationSection actionSection = itemSection.getConfigurationSection(action);

            if (actionSection.getKeys(false).contains("bannable"))
                return actionSection.getBoolean("bannable");
        }

        return false;
    }
}
