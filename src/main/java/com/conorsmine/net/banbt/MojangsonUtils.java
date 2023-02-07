package com.conorsmine.net.banbt;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.*;

public class MojangsonUtils {

    private ChatColor valCol = ChatColor.GREEN;
    private ChatColor valTypeCol = ChatColor.GREEN;
    private ChatColor tagCol = ChatColor.GOLD;
    private ChatColor objCol = ChatColor.AQUA;
    private ChatColor arrCol = ChatColor.LIGHT_PURPLE;
    private ChatColor specCol = ChatColor.RED;

    private boolean clickable = true;
    private boolean hoverable = true;
    private final Set<NBTType> clickTypes = new HashSet<>(Arrays.asList(NBTType.values()));

    private String cmdFormat = "/bn hidden_cmd %s";
    private String invalidClickTargetFormat = "/bn hidden_cmd INVALID %s";
    private String[] specialColorPaths = new String[0];

    ///////////////////////////////////////////////////////////////
    // Formatted, colored and interactable Mojangson
    ///////////////////////////////////////////////////////////////

    public BaseComponent[] getInteractiveMojangson(final NBTCompound compound, final String optionalPath) {
        final ComponentBuilder prettyString = new ComponentBuilder(objCol + "{§f");
        recursive(optionalPath, compound, prettyString).append(objCol + "}§f");

        return prettyString.create();
    }

    private ComponentBuilder recursive(String path, final NBTCompound compound, ComponentBuilder prettyString) {
        final Iterator<String> compoundIterator = compound.getKeys().iterator();
        while (compoundIterator.hasNext()) {
            final String key = compoundIterator.next();
            final NBTType type = compound.getType(key);
            final String newPath = (StringUtils.isBlank(path)) ? key : String.format("%s.%s", path, key);
            boolean shouldColor = isColoring(newPath);

            if (type == NBTType.NBTTagCompound)
                evaluateCompoundTag(compound, key, path, prettyString, !compoundIterator.hasNext(), shouldColor);

            else if (type == NBTType.NBTTagList)
                evaluateCompoundList(compound, key, newPath, prettyString, !compoundIterator.hasNext(), shouldColor);

            else
                evaluateSimpleCompound(compound, key, newPath, prettyString, !compoundIterator.hasNext(), shouldColor);
        }

        return prettyString;
    }

    private void evaluateSimpleCompound(final NBTCompound compound, String key, String path, ComponentBuilder prettyString, boolean lastCompound, boolean shouldColor) {
        String evaluatedString;
        if (shouldColor) evaluatedString = String.format(specCol + "%s: %s§f", key, evaluateSimpleCompoundToString(compound, key));
        else evaluatedString = String.format(tagCol + "%s: " + valCol + "%s§f", key, evaluateSimpleCompoundToString(compound, key));
        if (!lastCompound) evaluatedString += ", ";

        boolean isClickType = clickTypes.contains(compound.getType(key));
        final TextComponent pathDisplay = new TextComponent(evaluatedString);
        if (hoverable) pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(((isClickType) ? "§a" : "§c") + path).create()));
        if (clickable && isClickType) pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(cmdFormat, path)));
        else if (clickable) pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(invalidClickTargetFormat, path)));
        prettyString.append(pathDisplay);
    }

    private void evaluateCompoundList(final NBTCompound compound, String key, String path, ComponentBuilder prettyString, boolean lastCompound, boolean shouldColor) {
        String evaluatedString;
        if (shouldColor) evaluatedString = String.format("%s%s[§f", specCol, key);
        else evaluatedString = String.format("%s%s:%s[§f", tagCol, key, arrCol);

        boolean isClickType = clickTypes.contains(compound.getType(key));
        final TextComponent pathDisplay = new TextComponent(evaluatedString);
        if (hoverable) pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(((isClickType) ? "§a" : "§c") + path).create()));
        if (clickable && isClickType) pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(cmdFormat, path)));
        else if (clickable) pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(invalidClickTargetFormat, path)));
        prettyString.append(pathDisplay);

        final Iterator<ReadWriteNBT> compoundListIterator = compound.getCompoundList(key).iterator();
        int i = 0;
        while (compoundListIterator.hasNext()) {
            final ReadWriteNBT readWriteNBT = compoundListIterator.next();
            String newArrPath = String.format("%s[%d]", path, i);
            boolean colorArrObj = isColoring(newArrPath);

            if (colorArrObj) prettyString.append(String.format("%s{§f", specCol));
            else prettyString.append(String.format("%s{§f", objCol));
            recursive(newArrPath, (NBTCompound) readWriteNBT, prettyString);

            if (colorArrObj) prettyString.append(String.format("%s}§f", specCol));
            else prettyString.append(String.format("%s}§f", objCol));
            if (compoundListIterator.hasNext()) prettyString.append(", §f");

            i++;
        }


        if (shouldColor) prettyString.append(String.format("%s]§f", specCol));
        else prettyString.append(String.format("%s]§f", arrCol));
        if (!lastCompound) prettyString.append(", §f");
    }

    private void evaluateCompoundTag(final NBTCompound compound, String key, String path, ComponentBuilder prettyString, boolean lastCompound, boolean shouldColor) {
        String evaluatedString;
        if (shouldColor) evaluatedString = String.format("%s%s:%s{", specCol, key, specCol);
        else evaluatedString = String.format("%s%s:%s{", tagCol, key, objCol);

        boolean isClickType = clickTypes.contains(compound.getType(key));
        final TextComponent pathDisplay = new TextComponent(evaluatedString);
        String newPath = (StringUtils.isBlank(path)) ? key : String.format("%s.%s", path, key);
        if (hoverable) pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(((isClickType) ? "§a" : "§c") + newPath).create()));
        if (clickable && isClickType) pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(cmdFormat, newPath)));
        else if (clickable) pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(invalidClickTargetFormat, newPath)));
        prettyString.append(pathDisplay);

        recursive(newPath, compound.getCompound(key), prettyString);

        if (shouldColor) prettyString.append(specCol + "}§f");
        else prettyString.append(objCol + "}§f");
        if (!lastCompound) prettyString.append(", ");
    }

    private String evaluateSimpleCompoundToString(final NBTCompound compound, String key) {
        final NBTType type = compound.getType(key);

        if (type == NBTType.NBTTagInt) return String.valueOf(compound.getInteger(key));
        else if (type == NBTType.NBTTagLong) return compound.getLong(key) + "l§f";
        else if (type == NBTType.NBTTagByte) return compound.getByte(key) + "b§f";
        else if (type == NBTType.NBTTagFloat) return compound.getFloat(key) + "f§f";
        else if (type == NBTType.NBTTagShort) return compound.getShort(key) + "s§f";
        else if (type == NBTType.NBTTagDouble) return compound.getDouble(key) + "d§f";
        else if (type == NBTType.NBTTagString) return  "\"" + compound.getString(key) + "\"§f";
        else return "§cSOMETHING WENT WRONG§f";
    }



    ///////////////////////////////////////////////////////////////
    // Utils
    ///////////////////////////////////////////////////////////////

    private boolean isColoring(String newPath) {
        for (String colPath : specialColorPaths) {
            if (!removeArrIndexes(newPath).startsWith(removeArrIndexes(colPath))) continue;
            if (!arrIndexMatch(newPath, colPath)) continue;

            return true;
        }

        return false;
    }

    private boolean arrIndexMatch(String newPath, String colPath) {
        String[] pathKeyArr = pathToKeys(newPath);
        String[] colKeyArr = pathToKeys(colPath);

        for (int i = 0; i < colKeyArr.length; i++) {
            String pathKey = pathKeyArr[i];
            String colKey = colKeyArr[i];

            if (!isArr(pathKey)) continue;
            if (!isArr(colKey)) return false;
            if (isFullArray(colKey)) continue;
            if (getIndexOfArrayKey(pathKey) != getIndexOfArrayKey(colKey)) return false;
        }
        return true;
    }

    // Items[0].tag.Items[..] -> {Items[0], tag, Items[..]}
    public static String[] pathToKeys(final String path) {
        return path.split("(?<!\\.)\\.(?!\\.)");
    }

    // Items[0].tag.Items[..] -> Items[..]
    public static String getLastKey(final String path) {
        return path.replaceAll(".+\\.(?=\\w)", "");
    }

    // Items[0] -> true     id -> false
    public static boolean isArr(final String key) {
        if (key.length() <= 3) return false;
        return key.charAt(key.length() - 1) == ']';
    }

    // Items[..] -> true    Items[0] -> false
    public static boolean isFullArray(final String key) {
        return key.matches(".+\\.]$");
    }

    // Items[0] -> 0
    public static int getIndexOfArrayKey(final String key) {
        return Integer.parseInt(key.replaceAll(".+\\[|]$", ""));
    }

    // Items[..] -> Items   tag.ench[0].custom[..] -> tag.ench[].custom[]
    public static String removeArrIndexes(final String key) {
        return key.replaceAll("\\[((\\d+)|(\\.\\.))]", "[]");
    }






    ///////////////////////////////////////////////////////////////
    // Getters & Setters
    ///////////////////////////////////////////////////////////////


    public ChatColor getValCol() {
        return valCol;
    }

    public ChatColor getValTypeCol() {
        return valTypeCol;
    }

    public ChatColor getTagCol() {
        return tagCol;
    }

    public ChatColor getObjCol() {
        return objCol;
    }

    public ChatColor getArrCol() {
        return arrCol;
    }

    public ChatColor getSpecCol() {
        return specCol;
    }

    public boolean isClickable() {
        return clickable;
    }

    public final Set<NBTType> getClickTypes() {
        return clickTypes;
    }

    public boolean isHoverable() {
        return hoverable;
    }

    public String getCmdFormat() {
        return cmdFormat;
    }

    public String getInvalidClickTargetFormat() {
        return invalidClickTargetFormat;
    }

    public String[] getSpecialColorPaths() {
        return specialColorPaths;
    }

    public MojangsonUtils setValCol(ChatColor valCol) {
        this.valCol = valCol;
        return this;
    }

    public MojangsonUtils setValTypeCol(ChatColor valTypeCol) {
        this.valTypeCol = valTypeCol;
        return this;
    }

    public MojangsonUtils setTagCol(ChatColor tagCol) {
        this.tagCol = tagCol;
        return this;
    }

    public MojangsonUtils setObjCol(ChatColor objCol) {
        this.objCol = objCol;
        return this;
    }

    public MojangsonUtils setArrCol(ChatColor arrCol) {
        this.arrCol = arrCol;
        return this;
    }

    public MojangsonUtils setSpecCol(ChatColor specCol) {
        this.specCol = specCol;
        return this;
    }

    public MojangsonUtils setClickable(boolean clickable) {
        this.clickable = clickable;
        return this;
    }

    public MojangsonUtils clearClickTypes() {
        this.clickTypes.clear();
        return this;
    }

    public MojangsonUtils setClickTypes(NBTType... nbtTypes) {
        this.clickTypes.clear();
        this.clickTypes.addAll(Arrays.asList(nbtTypes));
        return this;
    }

    public MojangsonUtils addClickType(NBTType nbtType) {
        this.clickTypes.add(nbtType);
        return this;
    }

    public MojangsonUtils setHoverable(boolean hoverable) {
        this.hoverable = hoverable;
        return this;
    }

    public MojangsonUtils setCmdFormat(String cmdFormat) {
        this.cmdFormat = cmdFormat;
        return this;
    }

    public MojangsonUtils setInvalidClickTargetFormat(String invalidClickTargetFormat) {
        this.invalidClickTargetFormat = invalidClickTargetFormat;
        return this;
    }

    public MojangsonUtils setSpecialColorPaths(String... specialColorPaths) {
        this.specialColorPaths = specialColorPaths;
        return this;
    }
}
