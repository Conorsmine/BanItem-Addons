package com.conorsmine.net.banbt.files;


import com.conorsmine.net.banbt.BaNBT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * All instances of {@link com.conorsmine.net.banbt.autoBan.AutoBanManager autobanning}, by this plugin,
 * will be logged.
 */
@SuppressWarnings("unchecked")
public class BanFile {

    private static final String fileName = "bans.json";

    private final BaNBT pl;
    private File banFile;
    private JSONObject jsonLog;

    public BanFile(BaNBT pl) {
        this.pl = pl;
        this.banFile = createLogFile();
        this.jsonLog = parseFile(banFile);
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

    public void addBan(OfflinePlayer p, ItemStack item) {
        final UUID id = p.getUniqueId();
        JSONObject log = createPlayerBan(p.getName(), item);
        JSONArray playerLogs = getPlayerBans(id);
        playerLogs.add(log);
        jsonLog.put(id.toString(), playerLogs);
        saveLog();
    }

    private void saveLog() {
        try {
            FileWriter writer = new FileWriter(banFile);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(jsonLog));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        this.banFile = createLogFile();
        this.jsonLog = parseFile(banFile);
    }

    private JSONObject createPlayerBan(String playerName, ItemStack item) {
        boolean isNull = (item == null);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        JSONObject log = new JSONObject();
        log.put("playerName", playerName);
        log.put("timeFormatted", dtf.format(now));
        log.put("timeStamp", Instant.now().getEpochSecond());
        log.put("itemName", (isNull) ? null : item.getType().name());
        log.put("itemData", (isNull) ? null : NBTItem.convertItemtoNBT(item).toString());
        return log;
    }

    private JSONArray getPlayerBans(UUID id) {
        return ((JSONArray) jsonLog.getOrDefault(id.toString(), new JSONArray()));
    }

    public static String getFileName() {
        return fileName;
    }
}
