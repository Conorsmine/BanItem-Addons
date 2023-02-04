package com.conorsmine.net.banbt.mojangson;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;

import java.util.Iterator;

public class MojangsonUtils {

    private ChatColor valCol = ChatColor.GREEN;
    private ChatColor tagCol = ChatColor.GOLD;
    private ChatColor objCol = ChatColor.AQUA;
    private ChatColor arrCol = ChatColor.LIGHT_PURPLE;

    private String cmdFormat = "/bn hidden_cmd %s";

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
            final String newPath = (path.isEmpty()) ? key : String.format("%s.%s", path, key);

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
        String newPath = String.format("%s.%s", path, key);

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

    public void setValCol(ChatColor valCol) {
        this.valCol = valCol;
    }

    public void setTagCol(ChatColor tagCol) {
        this.tagCol = tagCol;
    }

    public void setObjCol(ChatColor objCol) {
        this.objCol = objCol;
    }

    public void setArrCol(ChatColor arrCol) {
        this.arrCol = arrCol;
    }

    public void setCmdFormat(String cmdFormat) {
        this.cmdFormat = cmdFormat;
    }
}
