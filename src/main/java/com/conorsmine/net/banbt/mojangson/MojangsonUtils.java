package com.conorsmine.net.banbt.mojangson;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.Iterator;
import java.util.function.Predicate;

public class MojangsonUtils {

    private ChatColor valCol = ChatColor.GREEN;
    private ChatColor tagCol = ChatColor.GOLD;
    private ChatColor objCol = ChatColor.AQUA;
    private ChatColor arrCol = ChatColor.LIGHT_PURPLE;

    private String cmdFormat = "/bn hidden_cmd %s";
    private String[] specialColorPaths = new String[0];

    ///////////////////////////////////////////////////////////////
    // Formatted, colored and interactable Mojangson
    ///////////////////////////////////////////////////////////////

    public BaseComponent[] getInteractiveMojangson(final NBTCompound compound, final String optionalPath) {
        final ComponentBuilder prettyString = new ComponentBuilder(objCol + "{§f");
        recursive(optionalPath, compound, prettyString).append(objCol + "}§f");

        return prettyString.create();
    }

    private ComponentBuilder recursive(String path, NBTCompound compound, ComponentBuilder prettyString) {
        final Iterator<String> compoundIterator = compound.getKeys().iterator();
        while (compoundIterator.hasNext()) {
            final String key = compoundIterator.next();
            final NBTType type = compound.getType(key);
            final String newPath = (StringUtils.isBlank(path)) ? key : String.format("%s.%s", path, key);
            boolean shouldColor = isColoring(newPath);

            if (type == NBTType.NBTTagCompound)
                evaluateCompoundTag(compound, key, path, prettyString, compoundIterator.hasNext());

            else if (type == NBTType.NBTTagList)
                evaluateCompoundList(compound, key, newPath, prettyString, compoundIterator.hasNext());

            else
                evaluateSimpleCompound(compound, key, newPath, prettyString, compoundIterator.hasNext());
        }

        return prettyString;
    }

    private void evaluateSimpleCompound(final NBTCompound compound, String key, String path, ComponentBuilder prettyString, boolean lastCompound) {
        String evaluatedString = String.format(tagCol + "%s: " + valCol + "%s§f", key, evaluateSimpleCompoundToString(compound, key));
        if (lastCompound) evaluatedString += ", ";

        final TextComponent pathDisplay = new TextComponent(evaluatedString);
        pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(path).create()));
        pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(cmdFormat, path)));
        prettyString.append(pathDisplay);
    }

    private void evaluateCompoundList(final NBTCompound compound, String key, String path, ComponentBuilder prettyString, boolean lastCompound) {
        final TextComponent pathDisplay = new TextComponent(String.format(tagCol + "%s:" + arrCol + "[§f", key));
        pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(path + "[..]").create()));
        pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(cmdFormat + "[..]", path)));
        prettyString.append(pathDisplay);

        final Iterator<ReadWriteNBT> compoundListIterator = compound.getCompoundList(key).iterator();
        int i = -1;
        while (compoundListIterator.hasNext()) {
            final ReadWriteNBT readWriteNBT = compoundListIterator.next();

            i++;

            prettyString.append(objCol + "{§f");
            recursive(String.format("%s[%d]", path, i), (NBTCompound) readWriteNBT, prettyString);

            if (compoundListIterator.hasNext()) prettyString.append(objCol + "}§f, ");
            else prettyString.append(objCol + "}§f");
        }


        if (lastCompound) prettyString.append(arrCol + "]§f, ");
        else prettyString.append(arrCol + "]§f");
    }

    private void evaluateCompoundTag(final NBTCompound compound, String key, String path, ComponentBuilder prettyString, boolean lastCompound) {
        final TextComponent pathDisplay = new TextComponent(String.format(tagCol + "%s:" + objCol + "{", key));
        String newPath = (StringUtils.isBlank(path)) ? key : String.format("%s.%s", path, key);

        pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(newPath).create()));
        pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(cmdFormat, newPath)));
        prettyString.append(pathDisplay);

        recursive(String.format("%s.%s", path, key), compound.getCompound(key), prettyString);

        if (lastCompound) prettyString.append(objCol + "}§f, ");
        else prettyString.append(objCol + "}§f");
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
        final String colorPath = getSimilarAny(newPath, specialColorPaths);
        boolean isColorPath = !StringUtils.isBlank(colorPath);
        final String arrPath = isAnyPathArr(specialColorPaths);

        if (!StringUtils.isBlank(arrPath)) isColorPath = (newPath.matches(String.format("%s.+", pathToNonRegex(arrPath))));
            return isColorPath;
    }

    private static String isAnyPathArr(final String[] paths) {
        for (String path : paths) {
            if (isArr(getLastKey(path))) return path;
        }
        return "";
    }

    private static String getSimilarAny(final String path, final String[] compareTo) {
        for (String s : compareTo) {
            if (isSimilar(path, s)) return path;
        }
        return "";
    }

    // Items[..].id  Items[0].id -> true     Items[0].id  Items[1].damage -> false
    private static boolean isSimilar(final String path, final String compareTo) {
        final String[] compareKeys = pathToKeys(compareTo);
        final String[] pathKeys = pathToKeys(path);

        if (path.equals(compareTo)) return true;
        if (compareKeys.length != pathKeys.length) return false;

        // Todo:
        //  If compareTo is not an Array, but path is => Return false
        //  If both are not an Array, but the tags aren't the same => Return false
        //  If both are an Array, but pathKey is a "full Array" => Continue
        //  If both are an Array, but don't have the same index => Return false

        for (int i = 0; i < compareKeys.length; i++) {
            final String compareKey = compareKeys[i];
            final String pathKey = pathKeys[i];
            boolean compareArr = isArr(compareKey);
            boolean pathArr = isArr(pathKey);

            if (compareArr != pathArr) return false;
            if (!getArrayKeyValue(compareKey).equals(getArrayKeyValue(pathKey))) return false;
            if (compareArr && isFullArray(compareKey)) continue;
            if (compareArr) {
                if (getIndexOfArrayKey(compareKey) != getIndexOfArrayKey(pathKey)) return false;
            }

        }

        return true;
    }

    public static String pathToNonRegex(final String path) {
        return path.replaceAll("\\.", "\\\\.").replaceAll("\\[", "\\\\[");
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

    public static String getArrayKeyValue(final String key) {
        return key.replaceAll("\\[\\d]$", "").replaceAll("\\[\\.{2}]$", "");
    }

    // Items[0] -> 0
    public static int getIndexOfArrayKey(final String key) {
        return Integer.parseInt(key.replaceAll(".+\\[|]$", ""));
    }






    ///////////////////////////////////////////////////////////////
    // Getters & Setters
    ///////////////////////////////////////////////////////////////


    public ChatColor getValCol() {
        return valCol;
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

    public String getCmdFormat() {
        return cmdFormat;
    }

    public String[] getSpecialColorPaths() {
        return specialColorPaths;
    }

    public MojangsonUtils setValCol(ChatColor valCol) {
        this.valCol = valCol;
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

    public MojangsonUtils setCmdFormat(String cmdFormat) {
        this.cmdFormat = cmdFormat;
        return this;
    }

    public MojangsonUtils setSpecialColorPaths(String[] specialColorPaths) {
        this.specialColorPaths = specialColorPaths;
        return this;
    }
}
