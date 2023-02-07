package com.conorsmine.net.banbt;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class AddAction {

    private static final Map<UUID, AddAction> actionMap = new HashMap<>();

    private final BaNBT pl;
    private final Player p;
    private final UUID actionID;
    private Set<String> compareStrings = new HashSet<>();

    public AddAction(BaNBT pl, Player p, String cmdHeader) {
        this.pl = pl;
        this.p = p;
        this.actionID = UUID.randomUUID();

        actionMap.put(actionID, this);
        initAction(cmdHeader);
    }

    private void initAction(String header) {
        p.sendMessage(pl.getCfgFile().getPrefix() + header);
        p.spigot().sendMessage(
                new MojangsonUtils()
                        .setCmdFormat("/bn hidden_cmd %s " + actionID.toString())
                        .getInteractiveMojangson(NBTItem.convertItemtoNBT(p.getInventory().getItemInMainHand()), "")
        );
    }



    public static void processActionCmd(CommandSender sender, String[] args) {
        System.out.println("HERE");
    }

    public static Map<UUID, AddAction> getActionMap() {
        return actionMap;
    }
}
