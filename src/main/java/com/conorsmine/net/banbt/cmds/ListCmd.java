package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.andross.banitem.items.CustomBannedItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
        final TextComponent itemName = new TextComponent();
        itemName.setColor(ChatColor.AQUA);

        for (CustomBannedItem bannedItem : getPlugin().getBanItemAPI().getCustomItems().getReversed().keySet()) {
            final String itemJson = /*NBTItem.convertItemtoNBT(bannedItem.toItemStack()).toString();*/ "{id:\"minecraft:spawn_egg\",Count:1b,tag:{EntityTag:{id:\"minecraft:rabbit\"}},Damage:0s}";
            itemName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[] { new TextComponent(itemJson) }));
            if (isPlayer) itemName.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(CMD_FORMAT, ((Player) sender).getUniqueId())));

            System.out.println(itemJson);
            itemName.setText(String.format("%s §7>> §b%s", prefix, bannedItem.getName()));
            sender.spigot().sendMessage(itemName);
        }
    }
}
