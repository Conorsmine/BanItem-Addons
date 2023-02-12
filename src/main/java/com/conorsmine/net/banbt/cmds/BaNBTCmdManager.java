package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import com.conorsmine.net.banbt.files.ConfigFile;
import com.conorsmine.net.banbt.banCustomItem.AddAction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class BaNBTCmdManager implements TabExecutor {

    private final BaNBT pl;
    private final ConfigFile cfg;
    private final Map<String, AbstractBanNBTCmd> commandMap = new HashMap<>();

    public BaNBTCmdManager(BaNBT pl) {
        this.pl = pl;
        this.cfg = pl.getCfgFile();

        commandMap.put("info", new InfoCmd("Info", pl, this));
        commandMap.put("reload", new ReloadCmd("Reload", pl, this));
        commandMap.put("add", new AddCmd("Add", pl, this));
        commandMap.put("list", new ListCmd("List", pl, this));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { sendCmdUsageMsg(sender); return false; }
        if (args[0].equals("hidden_cmd")) { AddAction.processActionCmd(sender, args); return false; }
        if (!commandMap.containsKey(args[0])) { sendCmdUsageMsg(sender); return false; }

        commandMap.get(args[0]).execute(sender, args);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return new ArrayList<>(getCmdNames());
        if (!commandMap.containsKey(args[0])) return null;

        return commandMap.get(args[0]).tabcomplete(sender, args);
    }

    public Set<String> getCmdNames() {
        return commandMap.keySet();
    }

    void sendCmdUsageMsg(CommandSender sender) {
        sender.sendMessage(cfg.getPrefix() + "§7§m     §r §l[§7§lUsage - §e§lv" + pl.getDescription().getVersion() + "§r§l] §7§m     ");
        commandMap.forEach((k, v) -> {
            if (v.isNeedsPlayer() && !(sender instanceof Player)) return;
            sender.sendMessage(String.format("%s §7- /bt §3%s§7: %s.", cfg.getPrefix(), k, v.getDescription()));
        });
    }

    void sendNonPlayerErr(CommandSender sender) {
        sender.sendMessage(String.format("%s§cOnly players can execute this command!", cfg.getPrefix()));
    }
}
