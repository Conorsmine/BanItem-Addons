package com.conorsmine.net.banbt.files;

import com.conorsmine.net.banbt.BaNBT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unchecked")
public class LogFile {

    private static final String fileName = "log.json";

    private final BaNBT pl;
    private final File logFile;
    private final JSONObject jsonLog;

    public LogFile(BaNBT pl) {
        this.pl = pl;
        this.logFile = createLogFile();
        this.jsonLog = parseFile(logFile);

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
        JSONArray playerLogs = getPlayerLogs(p);
        playerLogs.add(log);
        jsonLog.put(p.getUniqueId(), playerLogs);
        saveLog();
    }

    private void saveLog() {
        try {
            FileWriter writer = new FileWriter(logFile);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(jsonLog));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject createPlayerLog(OfflinePlayer p, ItemStack item, String reason) {
        boolean isNull = (item == null);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        JSONObject log = new JSONObject();
        log.put("playerName", p.getName());
        log.put("timeFormatted", dtf.format(now));
        log.put("timeStamp", Instant.now().getEpochSecond());
        log.put("reason", reason);
        log.put("itemName", (isNull) ? null : item.getType().name());
        log.put("itemData", (isNull) ? null : NBTItem.convertItemtoNBT(item).toString());
        return log;
    }

    private JSONArray getPlayerLogs(OfflinePlayer p) {
        return (JSONArray) jsonLog.getOrDefault(p.getUniqueId(), new JSONArray());
    }

    public static String getFileName() {
        return fileName;
    }
}
