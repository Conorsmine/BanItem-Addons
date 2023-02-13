package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import com.conorsmine.net.banbt.mojangson.MojangsonItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.andross.banitem.items.CustomBannedItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListCmd extends AbstractBanNBTCmd {

    private static final String CMD_FORMAT = "/bn list hidden_cmd %s";

    public ListCmd(String cmdName, BaNBT plugin, BaNBTCmdManager cmdManager) {
        super(cmdName, "provides a list of all custom banned items, including some data.", plugin, cmdManager);
    }

    @Override
    void execute(CommandSender sender, String[] args) {
        String prefix = getPlugin().getCfgFile().getPrefix();
        sender.sendMessage(getHeader());
        sender.sendMessage(String.format("%s§7All currently banned custom items:", prefix));
        sender.sendMessage(String.format("%s§7Note: Click on an item to view specifics.", prefix));

        sendBannedItemMsg(sender);
    }

    @Override
    List<String> tabcomplete(CommandSender sender, String[] args) {
        return null;
    }

    private void sendBannedItemMsg(CommandSender sender) {
        String prefix = getPlugin().getCfgFile().getPrefix();
        boolean isPlayer = (sender instanceof Player);

        FileConfiguration customItemConfig = getPlugin().getBanItemAPI().getCustomItems().getConfig();
        final TextComponent itemName = new TextComponent();
        itemName.setColor(ChatColor.AQUA);
        for (CustomBannedItem bannedItem : getPlugin().getBanItemAPI().getCustomItems().getReversed().keySet()) {
            final String itemJson = getBannedItemJson(customItemConfig, bannedItem.getName());
            if (itemJson == null) continue;

            itemName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(itemJson)}));
            if (isPlayer)
                itemName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(CMD_FORMAT, ((Player) sender).getUniqueId())));

            System.out.println(itemJson);
            itemName.setText(String.format("%s §7>> §b%s", prefix, bannedItem.getName()));
            sender.spigot().sendMessage(itemName);
        }
    }

    private String getBannedItemJson(FileConfiguration customItemConfig, String bannedItemName) {
        ConfigurationSection customItemData = customItemConfig.getConfigurationSection(bannedItemName);
        if (!validItemData(bannedItemName, customItemData)) return null;
        String itemType = customItemData.getString("material");
        if (!materialExists(bannedItemName, itemType)) return null;
        if (!validMaterial(bannedItemName, itemType)) return null;

        ItemStack customItem = new ItemStack(Material.valueOf(itemType), 1, (short) customItemData.getInt("durability", 0));
        if (!customItemData.getKeys(false).contains("nbtapi")) return NBTItem.convertItemtoNBT(customItem).toString();

        ConfigurationSection nbtApi = customItemData.getConfigurationSection("nbtapi");
        MojangsonItemBuilder itemBuilder = new MojangsonItemBuilder(customItem);

        for (String path : nbtApi.getKeys(false)) {
            Object data = nbtApi.get(path);
            if (data == null) {
                getPlugin().log(String.format("\"%s\" for custom item \"%s\" is missing data!", path, bannedItemName));
                continue;
            }

            String dataPath = String.format("tag.%s", path.replaceAll("#", "."));
            itemBuilder.addData(dataPath, data);
        }

        return itemBuilder.getItemCompound().toString();
    }

    private boolean validItemData(String bannedItemName, ConfigurationSection customItemData) {
        if (customItemData == null) {
            getPlugin().log(String.format("Missing data for item: \"%s\"", bannedItemName));
            return false;
        }

        return true;
    }

    private boolean materialExists(String bannedItemName, String itemType) {
        if(StringUtils.isBlank(itemType)) {
            getPlugin().log(String.format("Missing material for item: \"%s\"", bannedItemName));
            return false;
        }

        return true;
    }

    private boolean validMaterial(String bannedItemName, String itemType) {
        if (!Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toSet()).contains(itemType)) {
            getPlugin().log(String.format("Invalid material for item: \"%s\"", bannedItemName));
            return false;
        }

        return true;
    }
}
