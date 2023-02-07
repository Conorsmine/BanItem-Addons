package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import com.conorsmine.net.banbt.AddAction;
import fr.andross.banitem.actions.BanAction;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
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
        new AddAction(getPlugin(), p, getHeader());
    }

    @Override
    List<String> tabcomplete(CommandSender sender, String[] args) {
        if (args.length == 2) return Arrays.stream(BanAction.values()).map(BanAction::getName).collect(Collectors.toList());
        if (args.length == 4 && args[2].equals("-w")) return getPlugin().getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList());
        return null;
    }

    private void sendUsageMsg(CommandSender sender) {
        final String PREFIX = getPlugin().getCfgFile().getPrefix();
        sender.sendMessage(PREFIX + getHeader());
        sender.sendMessage(String.format("%s§7Usage:", PREFIX));
        sender.sendMessage(String.format("%s§b/bn add §3<actions> [-w worlds]", PREFIX));
        sender.sendMessage(String.format("%s§7 >> Will ban the currently held item.", PREFIX));
        sender.sendMessage(String.format("%s§7 >> If no worlds were entered,", PREFIX));
        sender.sendMessage(String.format("%s§7 >> it will default to the current.", PREFIX));
        sender.sendMessage(String.format("%s§2 Example: /bn add hold,use -w world", PREFIX));
    }

    private void sendNonItemErr(CommandSender sender) {
        sender.sendMessage(String.format("%s§cYou need to hold an item for this command!", getPlugin().getCfgFile().getPrefix()));
    }

    private void sendLastActionCanceledMsg(CommandSender sender) {
        sender.sendMessage(String.format("%s§cYour last action has been discarded.", getPlugin().getCfgFile().getPrefix()));
    }
}
