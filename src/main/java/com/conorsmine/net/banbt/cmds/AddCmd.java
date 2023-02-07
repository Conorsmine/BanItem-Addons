package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import com.conorsmine.net.banbt.AddAction;
import fr.andross.banitem.actions.BanAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class AddCmd extends AbstractBanNBTCmd{
    public AddCmd(String cmdName, BaNBT plugin, BaNBTCmdManager cmdManager) {
        super(cmdName, "Adds the currently held item to the BanItem plugin.", plugin, cmdManager);
    }

    @Override
    void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) { getCmdManager().sendNonPlayerErr(sender); return; }
        Player p = ((Player) sender);
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) { sendNonItemErr(sender); return; }
        if (args.length < 2) { sendUsageMsg(sender); return; }

        if (AddAction.getActionMap().containsKey(p.getUniqueId())) sendLastActionCanceledMsg(sender);
        sender.sendMessage(String.format("%s§aStarted new action.", getPlugin().getCfgFile().getPrefix()));

        Set<BanAction> banActions = parseBanActions(args, sender);
        World[] banWorlds = parseBanWorlds(args, sender);
        List<String> banMessages = parseBanMessages(args, sender);
        boolean useCurrent = parseBanItemCurrently(args, sender);

        if (banActions.isEmpty()) { sendNonActionErr(sender); return; }
        if (banWorlds.length == 0) banWorlds = new World[] { p.getWorld() };
        new AddAction(
                getPlugin(),
                p,
                getHeader(),
                banActions,
                banWorlds,
                banMessages,
                useCurrent
        );
    }

    @Override
    List<String> tabcomplete(CommandSender sender, String[] args) {
        if (args.length == 2) return Arrays.stream(BanAction.values()).map(BanAction::getName).collect(Collectors.toList());
        if (args.length == 4 && args[2].equals("-w")) return getPlugin().getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList());
        return null;
    }

    private Set<BanAction> parseBanActions(String[] args, CommandSender sender) {
        Set<String> allActions = Arrays.stream(BanAction.values()).map(BanAction::getName).collect(Collectors.toSet());
        Set<BanAction> banActions = new HashSet<>();
        for (int i = 1; i < args.length; i++) {
            String s = args[i].replace(",", "");
            if (s.equals("-w") || s.equals("-m") || s.equals("-b")) break;
            if (allActions.contains(s))
                banActions.add(BanAction.valueOf(s.toUpperCase(Locale.ROOT)));
            else
                sender.sendMessage(String.format("%s §7\"§3%s§7\" is not a valid action.", getPlugin().getCfgFile().getPrefix(), s));
        }

        return banActions;
    }

    private World[] parseBanWorlds(String[] args, CommandSender sender) {
        boolean found = false;
        Set<String> worldNames = getPlugin().getServer().getWorlds().stream().map(World::getName).collect(Collectors.toSet());
        Set<World> banWorlds = new HashSet<>();
        for (String s : args) {
            if (s.equals("-w")) found = true;
            if (s.equals("-m") || s.equals("-b")) break;
            if (!found) continue;
            if (worldNames.contains(s))
                banWorlds.add(getPlugin().getServer().getWorld(s));
            else
                sender.sendMessage(String.format("%s §7\"§3%s§7\" is not a valid world.", getPlugin().getCfgFile().getPrefix(), s));
        }

        return banWorlds.toArray(new World[0]);
    }

    private List<String> parseBanMessages(String[] args, CommandSender sender) {
        boolean found = false;
        List<String> banMessages = new LinkedList<>();
        for (String s : args) {
            if (s.equals("-m")) found = true;
            if (s.equals("-w") || s.equals("-b")) break;
            if (!found) continue;
            banMessages.add(s);
        }

        return banMessages;
    }

    private boolean parseBanItemCurrently(String[] args, CommandSender sender) {
        for (int i = 1; i < args.length - 2; i++) {
            if (args[i].equals("-b")) continue;
            if (args[i + 1].toLowerCase(Locale.ROOT).equals("true"))
                return true;
            else {
                sender.sendMessage(String.format("%s §7\"§3%s§7\" is not a valid boolean value.", getPlugin().getCfgFile().getPrefix(), args[i] + 1));
                sender.sendMessage(String.format("%s §7Defaulted to §3false§7.", getPlugin().getCfgFile().getPrefix()));
            }

        }

        return false;
    }






    private void sendUsageMsg(CommandSender sender) {
        final String PREFIX = getPlugin().getCfgFile().getPrefix();
        sender.sendMessage(PREFIX + getHeader());
        sender.sendMessage(String.format("%s§7Usage:", PREFIX));
        sender.sendMessage(String.format("%s§b/bn add §3<actions> [-w worlds] [-m message] [-b asIs]", PREFIX));
        sender.sendMessage(String.format("%s§7 >> Will ban the currently held item.", PREFIX));
        sender.sendMessage(String.format("%s§7 >> If no worlds were entered,", PREFIX));
        sender.sendMessage(String.format("%s§7 >> it will default to the current.", PREFIX));
        sender.sendMessage(String.format("%s§7 >> Set the §b-b§7 flag to §3true§7 to ban.", PREFIX));
        sender.sendMessage(String.format("%s§7 >> the item as is. (A shortcut per say)", PREFIX));
        sender.sendMessage(String.format("%s§2 Example: §b/bn add §3hold,use -w world§f", PREFIX));
    }

    private void sendNonItemErr(CommandSender sender) {
        sender.sendMessage(String.format("%s§cYou need to hold an item for this command!", getPlugin().getCfgFile().getPrefix()));
    }

    private void sendNonActionErr(CommandSender sender) {
        sender.sendMessage(String.format("%s §cAt least one action to ban needs to be specified!", getPlugin().getCfgFile().getPrefix()));
    }

    private void sendLastActionCanceledMsg(CommandSender sender) {
        sender.sendMessage(String.format("%s§cYour last action has been discarded.", getPlugin().getCfgFile().getPrefix()));
    }
}
