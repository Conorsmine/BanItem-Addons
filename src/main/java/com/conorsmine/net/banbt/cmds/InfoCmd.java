package com.conorsmine.net.banbt.cmds;

import com.conorsmine.net.banbt.BaNBT;
import org.bukkit.command.CommandSender;

import java.util.List;

public class InfoCmd extends AbstractBanNBTCmd {

    public InfoCmd(String cmdName, BaNBT plugin, BaNBTCmdManager cmdManager) {
        super(cmdName, "provides the NBT-Data of the handheld item", plugin, cmdManager);
    }

    @Override
    void execute(CommandSender sender, String[] args) {

    }

    @Override
    List<String> tabcomplete(CommandSender sender, String[] args) {
        return null;
    }
}
