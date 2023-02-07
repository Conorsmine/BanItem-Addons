package com.conorsmine.net.banbt;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import fr.andross.banitem.BanConfig;
import fr.andross.banitem.BanItem;
import fr.andross.banitem.actions.BanAction;
import fr.andross.banitem.actions.BanActionData;
import fr.andross.banitem.actions.BanDataType;
import fr.andross.banitem.database.items.CustomItems;
import fr.andross.banitem.items.CustomBannedItem;
import fr.andross.banitem.utils.debug.Debug;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class AddAction {

    private static final Map<UUID, AddAction> actionMap = new HashMap<>();
    private static final String finishActionCmdFormat = "/bn hidden_cmd %s FINISH";

    private final BaNBT pl;
    private final Player p;
    private final UUID actionID = UUID.randomUUID();
    private final String header;

    private final boolean banCurrent;
    private final Set<BanAction> banActions;
    private final World[] banWorlds;
    private final List<String> banMessages;
    private final Set<String> nbtDataStrings = new HashSet<>();
    private final NBTContainer itemNBT;

    private final MojangsonUtils mojangson = new MojangsonUtils()
            .setClickTypes(NBTType.NBTTagByte, NBTType.NBTTagDouble, NBTType.NBTTagFloat, NBTType.NBTTagInt, NBTType.NBTTagLong, NBTType.NBTTagShort, NBTType.NBTTagString)
            .setCmdFormat("/bn hidden_cmd " + actionID + " %s")
            .setInvalidClickTargetFormat("/bn hidden_cmd " + actionID + " INVALID %s ");

    public AddAction(BaNBT pl, Player p, String cmdHeader, Set<BanAction> banActions, World[] banWorlds, List<String> banMessages, boolean banCurrent) {
        this.pl = pl;
        this.p = p;
        this.header = cmdHeader;
        this.itemNBT = NBTItem.convertItemtoNBT(p.getInventory().getItemInMainHand());

        this.banCurrent = banCurrent;
        this.banActions = banActions;
        this.banWorlds = banWorlds;
        this.banMessages = banMessages;

        actionMap.put(actionID, this);
        initAction();
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

    private void completeAction(CommandSender sender) {
        pl.getBanItemAPI().getCustomItems()
                .put("smth", createCustomBannedItem(sender));

        pl.getBanItemAPI().addToBlacklist(
                createCustomBannedItem(sender),
                createBanActionData(),
                banWorlds
        );
        BanItem.getInstance().getListener().load(sender);

        p.sendMessage(String.format("%s %s", pl.getCfgFile().getPrefix(), header));
        // todo:
        p.sendMessage("Send some completion text here!");
        deleteAction();
    }

    private YamlConfiguration createConfigurationSection() {
        final YamlConfiguration nbtData = new YamlConfiguration();
        for (String path : nbtDataStrings) {
            // There is a weird quirk about the BanItem plugin that only "acknowledges" the "Damage"
            // if it's in the main configuration section
            if (path.equals("Damage")) continue;
            MojangsonUtils.NBTResult compoundResult = MojangsonUtils.getCompoundFromPath(itemNBT, path);
            Object dataFromNBT = MojangsonUtils.getSimpleDataFromCompound(compoundResult);

            nbtData.set(
                    path.replaceAll("\\.", "#"),
                    dataFromNBT
            );
        }

        final YamlConfiguration customItemConf = new YamlConfiguration();
        if (nbtDataStrings.contains("Damage")) customItemConf.set("damage", nbtDataStrings.stream().filter(s -> s.equals("Damage")).findFirst().get());
        customItemConf.set("material", NBTItem.convertNBTtoItem(itemNBT).getType().name().toLowerCase(Locale.ROOT));
        customItemConf.set("nbtapi", nbtData);
        return customItemConf;
    }

    private CustomBannedItem createCustomBannedItem(CommandSender sender) {
        BanItem banItemPlugin = BanItem.getInstance();
        Debug itemDebug = new Debug(banItemPlugin.getBanConfig(), sender);
        return new CustomBannedItem("smth", createConfigurationSection(), itemDebug);
    }

    private Map<BanAction, BanActionData> createBanActionData() {
        final Map<BanAction, BanActionData> actionData = new HashMap<>();
        final BanActionData banActionData = new BanActionData();
        banActionData.getMap().put(BanDataType.MESSAGE, banMessages);

        banActions.forEach(banAction -> actionData.put(banAction, banActionData));
        return actionData;
    }



    public static void processActionCmd(CommandSender sender, String[] args) {
        final AddAction action = getActionMap().getOrDefault(UUID.fromString(args[1]), null);
        if (action == null) return;
        if (args[2].equals("INVALID")) { action.sendInvalidTargetErr(args); return; }
        if (args[2].equals("FINISH")) { action.completeAction(sender); return; }

        action.toggleDataString(args[2]);
        action.sendNewDataMsg();
    }

    public static Map<UUID, AddAction> getActionMap() {
        return actionMap;
    }

    public Player getP() {
        return p;
    }

    public Set<String> getNbtDataStrings() {
        return nbtDataStrings;
    }
}
