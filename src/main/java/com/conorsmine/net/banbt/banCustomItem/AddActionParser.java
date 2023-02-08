package com.conorsmine.net.banbt.banCustomItem;

import fr.andross.banitem.actions.BanAction;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class AddActionParser {

    private final AddAction action;
    private final boolean banCurrent;
    private final Set<BanAction> banActions;
    private final World[] banWorlds;
    private final List<String> banMessages;

    public AddActionParser(AddAction action, String[] args) {
        this.action = action;

        this.banCurrent = parseBanItemCurrently(args, action.getPlayer());
        this.banActions = parseBanActions(args, action.getPlayer());
        this.banWorlds = parseBanWorlds(args, action.getPlayer());
        this.banMessages = parseBanMessages(args);
    }

    private Set<BanAction> parseBanActions(String[] args, CommandSender sender) {
        Set<String> allActions = Arrays.stream(BanAction.values()).map(BanAction::getName).collect(Collectors.toSet());
        Set<BanAction> banActions = new HashSet<>();
        for (int i = 1; i < args.length; i++) {
            String[] actionArr = args[i].split(",");

            for (String s : actionArr) {
                if (s.equals("-w") || s.equals("-m") || s.equals("-b")) return banActions;
                if (allActions.contains(s))
                    banActions.add(BanAction.valueOf(s.toUpperCase(Locale.ROOT)));
                else
                    sender.sendMessage(String.format("%s §7\"§3%s§7\" is not a valid action.", action.getPlugin().getCfgFile().getPrefix(), s));
            }
        }

        return banActions;
    }

    private World[] parseBanWorlds(String[] args, CommandSender sender) {
        boolean found = false;
        Set<String> worldNames = action.getPlugin().getServer().getWorlds().stream().map(World::getName).collect(Collectors.toSet());
        Set<World> banWorlds = new HashSet<>();
        for (String worlds : args) {
            if (worlds.equals("-w")) { found = true; continue; }


            String[] worldArr = worlds.split(",");
            for (String s : worldArr) {
                if (!found) break;
                if (s.equals("-m") || s.equals("-b"))  { found = false; break; }
                if (worldNames.contains(s)) {
                    banWorlds.add(action.getPlugin().getServer().getWorld(s));}
                else
                    sender.sendMessage(String.format("%s §7\"§3%s§7\" is not a valid world.", action.getPlugin().getCfgFile().getPrefix(), s));
            }
        }
        if (banWorlds.isEmpty()) banWorlds.add(((Player) sender).getWorld());

        return banWorlds.toArray(new World[0]);
    }

    private List<String> parseBanMessages(String[] args){
        boolean found = false;
        List<String> banMessages = new LinkedList<>();
        for (String s : args) {
            if (s.equals("-m")) { found = true; continue; }
            if (s.equals("-w") || s.equals("-b")) continue;
            if (!found) continue;
            banMessages.add(s);
        }

        return banMessages;
    }

    private boolean parseBanItemCurrently(String[] args, CommandSender sender) {
        for (int i = 1; i < args.length - 2; i++) {
            if (!args[i].equals("-b")) continue;
            if (args[i + 1].toLowerCase(Locale.ROOT).equals("true"))
                return true;
            else {
                sender.sendMessage(String.format("%s §7\"§3%s§7\" is not a valid boolean value.", action.getPlugin().getCfgFile().getPrefix(), args[i + 1]));
                sender.sendMessage(String.format("%s §7Defaulted to §3false§7.", action.getPlugin().getCfgFile().getPrefix()));
            }

        }

        return false;
    }



    public AddAction getAction() {
        return action;
    }

    public boolean isBanCurrent() {
        return banCurrent;
    }

    public Set<BanAction> getBanActions() {
        return banActions;
    }

    public World[] getBanWorlds() {
        return banWorlds;
    }

    public List<String> getBanMessages() {
        return banMessages;
    }
}
