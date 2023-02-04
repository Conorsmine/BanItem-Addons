package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import com.conorsmine.net.banbt.MojangsonUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InfoCmd extends AbstractBanNBTCmd {

    public InfoCmd(String cmdName, BaNBT plugin, BaNBTCmdManager cmdManager) {
        super(cmdName, "provides the NBT-Data of the handheld item", plugin, cmdManager);
        setNeedsPlayer(true);
    }

    @Override
    void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) { getCmdManager().sendNonPlayerErr(sender); return; }
        Player p = ((Player) sender);
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) { sendNonItemErr(sender); return; }

        sender.sendMessage(getPlugin().getCfgFile().getPrefix() + getHeader());
        sender.spigot().sendMessage(MojangsonUtils.getPathColoredMojangson(NBTItem.convertItemtoNBT(item), ""));
    }

    @Override
    List<String> tabcomplete(CommandSender sender, String[] args) {
        return null;
    }

    private void sendNonItemErr(CommandSender sender) {
        sender.sendMessage(String.format("%s§cYou need to hold an item for this command!", getPlugin().getCfgFile().getPrefix()));
    }
}
