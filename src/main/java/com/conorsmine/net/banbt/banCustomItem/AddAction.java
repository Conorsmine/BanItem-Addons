package com.conorsmine.net.banbt.banCustomItem;

import com.conorsmine.net.banbt.BaNBT;
import com.conorsmine.net.banbt.MojangsonUtils;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class AddAction {

    private static final Map<UUID, AddAction> actionMap = new HashMap<>();
    private static final String finishActionCmdFormat = "/bn hidden_cmd %s FINISH";

    private final BaNBT pl;
    private final Player p;
    private final UUID actionID = UUID.randomUUID();
    private final String header;

    private final AddActionParser actionParser;
    private final CustomItemBanner banCustomItemBuilder;
    private final Set<String> nbtDataStrings = new HashSet<>();
    private final NBTContainer itemNBT;

    private final MojangsonUtils mojangson = new MojangsonUtils()
            .setClickTypes(MojangsonUtils.SIMPLE_TYPES.toArray(new NBTType[0]))
            .setCmdFormat("/bn hidden_cmd " + actionID + " %s")
            .setInvalidClickTargetFormat("/bn hidden_cmd " + actionID + " INVALID %s ");

    public AddAction(BaNBT pl, Player p, String cmdHeader, String[] args) {
        this.pl = pl;
        this.p = p;
        this.header = cmdHeader;
        this.itemNBT = NBTItem.convertItemtoNBT(p.getInventory().getItemInMainHand());
        this.actionParser = new AddActionParser(this, args);
        this.banCustomItemBuilder = new CustomItemBanner(this);

        actionMap.put(actionID, this);

        if (!actionParser.isBanCurrent())
            initAction();
        else completeAction();
    }

    private void initAction() {
        sendUsageMsg();
        p.spigot().sendMessage(mojangson.getInteractiveMojangson(itemNBT, ""));
    }

    private void deleteAction() {
        actionMap.remove(actionID);
    }

    private void clearChat() {
        p.sendMessage(new String[20]);
    }

    private void sendHeader() {
        p.sendMessage(pl.getCfgFile().getPrefix() + header);
    }

    private void sendUsageMsg() {
        final String PREFIX = pl.getCfgFile().getPrefix();
        sendHeader();
        p.sendMessage(String.format("%s§7Usage:", PREFIX));
        p.sendMessage(String.format("%s§7 >> Select in the following NBT", PREFIX));
        p.sendMessage(String.format("%s§7 >> all data that should be considered.", PREFIX));
        p.sendMessage(String.format("%s§7 >> After doing so specify what the value", PREFIX));
        p.sendMessage(String.format("%s§7 >> it should be.", PREFIX));
        p.sendMessage(String.format("%s§7 >> When all the data has been selected", PREFIX));
        p.sendMessage(String.format("%s§7 >> and configured, press on the §a\"FINISH\"§7 button.", PREFIX));
        p.sendMessage(String.format("%s§7 >> Note: You can undo a selection by clicking on the path again.", PREFIX));
        p.sendMessage(String.format("%s§7 >> Note: You can only select data which has §aGREEN§7 hovertext.", PREFIX));
        p.sendMessage(String.format("%s§7 >> Note: This process will take up your chat.", PREFIX));
        p.sendMessage(String.format("%s§7 >> Note: Use §b/bn info §7to get basic info.", PREFIX));
    }

    private void sendNewDataMsg() {
        clearChat();
        sendHeader();
        p.spigot().sendMessage(mojangson
                .setSpecialColorPaths(nbtDataStrings.toArray(new String[0]))
                .getInteractiveMojangson(itemNBT, "")
        );

        p.sendMessage("");
        final TextComponent finishButton = new TextComponent("FINISH");
        finishButton.setColor(ChatColor.GREEN);
        finishButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(finishActionCmdFormat, actionID)));
        finishButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Finished configuring?").create()));
        p.spigot().sendMessage(finishButton);
    }

    private void sendInvalidTargetErr(String[] args) {
        p.sendMessage(String.format("%s §7\"§b%s§7\" is not a valid path!", pl.getCfgFile().getPrefix(), args[3]));
        p.sendMessage(String.format("%s §7All valid data options have §aGREEN§7 hover text.", pl.getCfgFile().getPrefix()));
    }

    private void toggleDataString(String data) {
        if (nbtDataStrings.contains(data))
            nbtDataStrings.remove(data);
        else
            nbtDataStrings.add(data);
    }

    private void completeAction() {
        banCustomItemBuilder.addCustomBannedItem();

        p.sendMessage(String.format("%s %s", pl.getCfgFile().getPrefix(), header));
        // todo:
        p.sendMessage("Send some completion text here!");
        deleteAction();
    }



    public static void processActionCmd(CommandSender sender, String[] args) {
        final AddAction action = getActionMap().getOrDefault(UUID.fromString(args[1]), null);
        if (action == null) return;
        if (args[2].equals("INVALID")) { action.sendInvalidTargetErr(args); return; }
        if (args[2].equals("FINISH")) { action.completeAction(); return; }

        action.toggleDataString(args[2]);
        action.sendNewDataMsg();
    }

    public static Map<UUID, AddAction> getActionMap() {
        return actionMap;
    }

    public Player getPlayer() {
        return p;
    }

    public BaNBT getPlugin() {
        return pl;
    }

    public AddActionParser getActionParser() {
        return actionParser;
    }

    public NBTContainer getItemNBT() {
        return itemNBT;
    }

    public Set<String> getNbtDataStrings() {
        return nbtDataStrings;
    }

    public Set<String> addNbtDataStrings(final Set<String> data) {
        nbtDataStrings.addAll(data);
        return nbtDataStrings;
    }
}
