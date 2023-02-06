package com.conorsmine.net.banbt.files;

import com.conorsmine.net.banbt.BaNBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

@SuppressWarnings("unchecked")
public class LogFile {

    private static final String fileName = "log.json";

    private final BaNBT pl;
    private final JSONObject jsonLog;

    public LogFile(BaNBT pl) {
        this.pl = pl;
        this.jsonLog = parseFile(createLogFile());

    }

    private File createLogFile() {
        File file = new File(pl.getDataFolder().getAbsolutePath() + File.separator + fileName);
        if (file.exists()) return file;

        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("{}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            pl.log("§cCould not create log file at \"" + pl.getDataFolder().getAbsolutePath() + File.pathSeparator + fileName + "\"!");
        }

        return file;
    }

    private JSONObject parseFile(File file) {
        JSONParser parser = new JSONParser();
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            return (JSONObject) parser.parse(reader);

        } catch (ParseException | IOException e) {
            e.printStackTrace();
            pl.log("§cCould not parse log file!\nPlease make sure that the §7" + fileName + "§c file is valid JSON!");
        }

        return null;
    }

    public void addLog(OfflinePlayer p, ItemStack item, String reason) {
        JSONObject log = createPlayerLog(p, item, reason);
        JSONArray playerLogs = getPlayerPlayerLogs(p);
        playerLogs.add(log);
        jsonLog.put(p.getUniqueId(), playerLogs);
    }

    private JSONObject createPlayerLog(OfflinePlayer p, ItemStack item, String reason) {
        boolean isNull = (item == null);
        JSONObject log = new JSONObject();
        log.put("playerName", p.getName());
        log.put("reason", reason);
        log.put("itemName", (isNull) ? null : item.getType().name());
        log.put("itemData", (isNull) ? null : new NBTItem(item).toString());
        return log;
    }

    private JSONArray getPlayerPlayerLogs(OfflinePlayer p) {
        return (JSONArray) jsonLog.getOrDefault(p.getUniqueId(), new JSONArray());
    }

    public static String getFileName() {
        return fileName;
    }
}
