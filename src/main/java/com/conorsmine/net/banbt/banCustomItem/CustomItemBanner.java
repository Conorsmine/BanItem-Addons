package com.conorsmine.net.banbt.banCustomItem;

import com.conorsmine.net.banbt.BaNBT;
import com.conorsmine.net.banbt.MojangsonUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.andross.banitem.BanItem;
import fr.andross.banitem.actions.BanAction;
import fr.andross.banitem.items.CustomBannedItem;
import fr.andross.banitem.utils.debug.Debug;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

public class CustomItemBanner {

    private final AddAction action;
    private final BaNBT pl;

    public CustomItemBanner(AddAction action) {
        this.action = action;
        this.pl = action.getPlugin();
    }

    public void addCustomBannedItem() {
        final CustomBannedItem customBannedItem = createCustomBannedItem();
        addItemToCustomConfig(customBannedItem);
        addItemToConfig(customBannedItem);
    }

    private void addItemToCustomConfig(CustomBannedItem customBannedItem) {
        FileConfiguration customConfig = BanItem.getInstance().getBanDatabase().getCustomItems().getConfig();
        customConfig.set(customBannedItem.getName(), createCustomItemBanData());
        try {
            customConfig.save(BanItem.getInstance().getBanDatabase().getCustomItems().getFile());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void addItemToConfig(CustomBannedItem customBannedItem) {
        final FileConfiguration banConfig = BanItem.getInstance().getConfig();
        final Map<String, List<String>> banData = createBanActionData();
        ConfigurationSection blacklist = new YamlConfiguration();
        if (banConfig.contains("blacklist")) blacklist = banConfig.getConfigurationSection("blacklist");
        if (blacklist == null) blacklist = new YamlConfiguration();

        for (World world : action.getActionParser().getBanWorlds()) {
            ConfigurationSection worldSection = new YamlConfiguration();
            if (blacklist.contains(world.getName())) worldSection = blacklist.getConfigurationSection(world.getName());
            System.out.println(worldSection);

            worldSection.set(customBannedItem.getName(), banData);
            blacklist.set(world.getName(), worldSection);
        }

        banConfig.set("blacklist", blacklist);

        try {
            banConfig.save(BanItem.getInstance().getBanConfig().getConfigFile());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private CustomBannedItem createCustomBannedItem() {
        BanItem banItemPlugin = BanItem.getInstance();
        Debug itemDebug = new Debug(banItemPlugin.getBanConfig(), action.getPlayer());
        return new CustomBannedItem(generateBanItemKey(), createConfigurationSection(), itemDebug);
    }

    private YamlConfiguration createConfigurationSection() {
        final YamlConfiguration nbtData = new YamlConfiguration();
        final YamlConfiguration customItemConf = new YamlConfiguration();
        boolean damageSelected = action.getNbtDataStrings().contains("Damage");

        for (String path : action.getNbtDataStrings()) {
            // There is a weird quirk about the BanItem plugin that only "acknowledges" the "Damage"
            // if it's in the main configuration section and named "durability"
            if (path.equals("Damage")) continue;
            MojangsonUtils.NBTResult compoundResult = MojangsonUtils.getCompoundFromPath(action.getItemNBT(), path);
            Object dataFromNBT = MojangsonUtils.getSimpleDataFromCompound(compoundResult);

            nbtData.set(
                    path.replaceAll("\\.", "#"),
                    dataFromNBT
            );
        }

        // Todo:
        //  For some reason this isn't working and idk why
        //  Whatever I do, I cannot make the "customItemConf" add "durability"
        final ItemStack item = NBTItem.convertNBTtoItem(action.getItemNBT());
        if (damageSelected) customItemConf.set("durability", item.getDurability());
        customItemConf.set("material", item.getType().name().toLowerCase(Locale.ROOT));
        customItemConf.set("nbtapi", nbtData);
        return customItemConf;
    }

    private Map<String, List<String>> createBanActionData() {
        final Map<String, List<String>> actionData = new HashMap<>();
        for (BanAction banAction : action.getActionParser().getBanActions()) {
            actionData.put(
                    banAction.getName(),
                    action.getActionParser().getBanMessages());
        }

        return actionData;
    }

    private Map<String, Object> createCustomItemBanData() {
        Map<String, Object> banMap = new HashMap<>();
        banMap.put("material", NBTItem.convertNBTtoItem(action.getItemNBT()).getType().name());
        banMap.put("nbtapi", createNBTBanData());

        return banMap;
    }

    private Map<String, Object> createNBTBanData() {
        Map<String, Object> nbtData = new HashMap<>();
        for (String path : action.getNbtDataStrings()) {
            MojangsonUtils.NBTResult result = MojangsonUtils.getCompoundFromPath(action.getItemNBT(), path);
            Object data = MojangsonUtils.getSimpleDataFromCompound(result);

            if (MojangsonUtils.getFirstKey(path).equals("tag")) path = path.replaceFirst("tag\\.", "");
            nbtData.put(path.replaceAll("\\.", "#"), data);
        }

        return nbtData;
    }

    private String generateBanItemKey() {
        String key = NBTItem.convertNBTtoItem(action.getItemNBT()).getType().name().toLowerCase(Locale.ROOT);
        FileConfiguration blacklist = BanItem.getInstance().getConfig();

        if (!blacklist.contains("blacklist")) return key + "_0";
        for (String world : blacklist.getKeys(false)) {

            if (!blacklist.getConfigurationSection(world).getKeys(false).isEmpty()) return key + "_0";
            for (String itemKey : blacklist.getConfigurationSection(world).getKeys(false)) {
                if (!itemKey.startsWith(key)) continue;

                return String.format("%s_%d", key, Integer.parseInt(itemKey.replace(key + "_", "")));
            }
        }

        return key + "_0";
    }
}
