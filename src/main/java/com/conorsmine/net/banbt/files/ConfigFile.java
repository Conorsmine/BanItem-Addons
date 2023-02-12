package com.conorsmine.net.banbt.files;

import com.conorsmine.net.banbt.BaNBT;
import fr.andross.banitem.actions.BanAction;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class ConfigFile {

    private final BaNBT pl;
    private FileConfiguration config;
    private String prefix = "§c§l[§e§lBaNBT§c§l] ";
    private boolean logging = false;
    private boolean bannable = false;
    private BanAction[] logActions = new BanAction[0];

    public ConfigFile(BaNBT pl) {
        this.pl = pl;
        pl.saveDefaultConfig();
        config = pl.getConfig();
    }

    public void initData() {
        prefix = getOrDefault("prefix", prefix);
        logging = getOrDefault("enableLogging", false);
        bannable = getOrDefault("enableBannable", false);
        logActions = processLogActions(getOrDefault("logActions", ""));
    }

    public void reload() {
        pl.reloadConfig();
        config = pl.getConfig();
        pl.getBanManager().reloadBannableItemsFromConfig();
        initData();
    }

    private String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private <E> E getOrDefault(String path, E other) {
        final Object o = config.get(path, other);

        if (o instanceof String) return (E) c((String) o);
        else if (o instanceof List && ((List<E>) config.get(path, other)).set(0, null) instanceof String) {
            List<String> strList = new ArrayList<>();
            ((List<String>) o).forEach(s -> strList.add(c(s)));
            return (E) strList;
        }


        return (E) config.get(path, other);
    }

    private BanAction[] processLogActions(String s) {
        s = s.replaceAll("\\s", "").toLowerCase(Locale.ROOT);
        if (s.equals("*")) return BanAction.values();
        String[] actionArr = s.split(",");
        Set<BanAction> finalActionSet = new HashSet<>();

        // Only adds valid actions
        // Too much nesting imo
        for (String action : actionArr) {
            Iterator<BanAction> banActionIterator = Arrays.stream(BanAction.values()).iterator();
            while (banActionIterator.hasNext()) {
                BanAction banAction = banActionIterator.next();
                if (!action.equals(banAction.getName())) {
                    if (!banActionIterator.hasNext())
                        pl.log(String.format("§f\"§7%s§f\"§c is not a valid action to be logged!", action));
                    continue;
                }

                finalActionSet.add(banAction);
                break;
            }
        }

        return finalActionSet.parallelStream().toArray(BanAction[]::new);
    }



    public String getPrefix() {
        return prefix + "§r";
    }

    public boolean isLogging() {
        return logging;
    }

    public boolean isBannable() {
        return bannable;
    }

    public BanAction[] getLogActions() {
        return logActions;
    }
}
