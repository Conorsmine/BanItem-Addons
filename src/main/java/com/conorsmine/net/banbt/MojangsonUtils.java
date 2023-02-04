package com.conorsmine.net.banbt;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MojangsonUtils {

    ///////////////////////////////////////////////////////////////
    // Formatted and colored Mojangson
    ///////////////////////////////////////////////////////////////

    public static BaseComponent[] getPredicateColoredMojangson(final NBTCompound compound, final Predicate<String> check) {
        final ComponentBuilder prettyString = new ComponentBuilder("§b{§f");
        colorRecursive("", compound, prettyString, check).append("§b}§f");

        return prettyString.create();
    }

    public static BaseComponent[] getPathColoredMojangson(final NBTCompound compound, final String... path) {
        return getPredicateColoredMojangson(compound, createColorPathPredicate(path));
    }

    private static ComponentBuilder colorRecursive(String path, NBTCompound compound, ComponentBuilder prettyString, Predicate<String> check) {
        final Iterator<String> compoundIterator = compound.getKeys().iterator();
        while (compoundIterator.hasNext()) {
            final String key = compoundIterator.next();
            final NBTType type = compound.getType(key);

            final String newPath = (path.isBlank()) ? key : String.format("%s.%s", path, key);
            boolean shouldColor = check.test(newPath);

            if (type == NBTType.NBTTagCompound) {
                evaluateColorCompoundTag(compound, key, path, prettyString, compoundIterator.hasNext(), check);
                continue;
            }

            if (type != NBTType.NBTTagList) {
                evaluateColorSimpleCompound(compound, key, newPath, prettyString, compoundIterator.hasNext(), shouldColor);
                continue;
            }


            evaluateColorCompoundList(compound, key, newPath, prettyString, compoundIterator.hasNext(), check, shouldColor);
        }

        return prettyString;
    }

    private static void evaluateColorSimpleCompound(final NBTCompound compound, String key, String path, ComponentBuilder prettyString,
                                                    boolean lastCompound, boolean isColorPath) {
        String evaluatedString;
        if (isColorPath) evaluatedString = String.format("§5%s: %s§f", key, evaluateSimpleCompoundToString(compound, key));
        else evaluatedString = String.format("§6%s: §a%s§f", key, evaluateSimpleCompoundToString(compound, key));
        if (lastCompound) evaluatedString += ", ";

        final TextComponent pathDisplay = new TextComponent(evaluatedString);
        pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(path).create()));
        prettyString.append(pathDisplay);
    }

    private static void evaluateColorCompoundList(final NBTCompound compound, String key, String path, ComponentBuilder prettyString,
                                                  boolean lastCompound, Predicate<String> check, boolean isColorPath) {
        final TextComponent pathDisplay = new TextComponent(String.format("§6%s:§d[§f", key));
        if (isColorPath) pathDisplay.setText(String.format("§5%s:§d[§f", key));
        pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(path + "[..]").create()));
        prettyString.append(pathDisplay);

        final Iterator<ReadWriteNBT> compoundListIterator = compound.getCompoundList(key).iterator();
        int i = -1;
        while (compoundListIterator.hasNext()) {
            final ReadWriteNBT readWriteNBT = compoundListIterator.next();
            i++;

            prettyString.append("§b{§f");
            colorRecursive(String.format("%s[%d]", path, i), (NBTCompound) readWriteNBT, prettyString, check);

            if (compoundListIterator.hasNext()) prettyString.append("§b}§f, ");
            else prettyString.append("§b}§f");
        }


        if (lastCompound) prettyString.append("§d]§f, ");
        else prettyString.append("§d]§f");
    }

    private static void evaluateColorCompoundTag(final NBTCompound compound, String key, String path, ComponentBuilder prettyString,
                                                 boolean lastCompound, Predicate<String> check) {
        final TextComponent pathDisplay = new TextComponent(String.format("§6%s:§b{", key));

        pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.format("%s.%s", path, key)).create()));
        prettyString.append(pathDisplay);

        colorRecursive(String.format("%s.%s", path, key), compound.getCompound(key), prettyString, check);

        if (lastCompound) prettyString.append("§b}§f, ");
        else prettyString.append("§b}§f");
    }

    private static Predicate<String> createColorPathPredicate(String[] colorPathArr) {
        return newPath -> {
            final String colorPath = getSimilarAny(newPath, colorPathArr);
            boolean isColorPath = !colorPath.isBlank();
            final String arrPath = isAnyPathArr(colorPathArr);
            if (!arrPath.isBlank()) isColorPath = (newPath.matches(String.format("%s.+", pathToNonRegex(arrPath))));
            return isColorPath;
        };
    }

    private static String isAnyPathArr(final String[] paths) {
        for (String path : paths) {
            if (isArr(getLastKey(path))) return path;
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

    private static String getSimilarAny(final String path, final String[] compareTo) {
        for (String s : compareTo) {
            if (isSimilar(path, s)) return path;
        }
        return "";
    }



    ///////////////////////////////////////////////////////////////
    // Formatted, colored and interactable Mojangson
    ///////////////////////////////////////////////////////////////

    public static BaseComponent[] getInteractiveMojangson(final NBTCompound compound, final String optionalPath) {
        final ComponentBuilder prettyString = new ComponentBuilder("§b{§f");
        recursive(optionalPath, compound, prettyString).append("§b}§f");

        return prettyString.create();
    }

    private static ComponentBuilder recursive(String path, NBTCompound compound, ComponentBuilder prettyString) {
        final Iterator<String> compoundIterator = compound.getKeys().iterator();
        while (compoundIterator.hasNext()) {
            final String key = compoundIterator.next();
            final NBTType type = compound.getType(key);
            final String newPath = (path.isBlank()) ? key : String.format("%s.%s", path, key);

            if (type == NBTType.NBTTagCompound)
                evaluateCompoundTag(compound, key, path, prettyString, compoundIterator.hasNext());

            else if (type == NBTType.NBTTagList)
                evaluateCompoundList(compound, key, newPath, prettyString, compoundIterator.hasNext());

            else
                evaluateSimpleCompound(compound, key, newPath, prettyString, compoundIterator.hasNext());
        }

        return prettyString;
    }

    private static void evaluateSimpleCompound(final NBTCompound compound, String key, String path, ComponentBuilder prettyString, boolean lastCompound) {
        String evaluatedString = String.format("§6%s: §a%s§f", key, evaluateSimpleCompoundToString(compound, key));
        if (lastCompound) evaluatedString += ", ";

        final TextComponent pathDisplay = new TextComponent(evaluatedString);
        pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(path).create()));
        pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/sellwand debug_hidden " + path)));
        prettyString.append(pathDisplay);
    }

    private static void evaluateCompoundList(final NBTCompound compound, String key, String path, ComponentBuilder prettyString, boolean lastCompound) {
        final TextComponent pathDisplay = new TextComponent(String.format("§6%s:§d[§f", key));
        pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(path + "[..]").create()));
        pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ("/sellwand debug_hidden " + path + "[..]")));
        prettyString.append(pathDisplay);

        final Iterator<ReadWriteNBT> compoundListIterator = compound.getCompoundList(key).iterator();
        int i = -1;
        while (compoundListIterator.hasNext()) {
            final ReadWriteNBT readWriteNBT = compoundListIterator.next();

            i++;

            prettyString.append("§b{§f");
            recursive(String.format("%s[%d]", path, i), (NBTCompound) readWriteNBT, prettyString);

            if (compoundListIterator.hasNext()) prettyString.append("§b}§f, ");
            else prettyString.append("§b}§f");
        }


        if (lastCompound) prettyString.append("§d]§f, ");
        else prettyString.append("§d]§f");
    }

    private static void evaluateCompoundTag(final NBTCompound compound, String key, String path, ComponentBuilder prettyString, boolean lastCompound) {
        final TextComponent pathDisplay = new TextComponent(String.format("§6%s:§b{", key));

        pathDisplay.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.format("%s.%s", path, key)).create()));
        pathDisplay.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/sellwand debug_hidden %s.%s", path, key)));
        prettyString.append(pathDisplay);

        recursive(String.format("%s.%s", path, key), compound.getCompound(key), prettyString);

        if (lastCompound) prettyString.append("§b}§f, ");
        else prettyString.append("§b}§f");
    }

    private static void evaluateCompoundFullList(final NBTCompoundList compoundList, String arrKeyPath, ComponentBuilder prettyString) {
        for (int i = 0; i < compoundList.size(); i++) {
            final NBTCompound compound = compoundList.get(i);
            final String path = String.format("%s[%d]", arrKeyPath, i);

            prettyString.append("§b{§f");
            recursive(path, compound, prettyString);

            if (i == compoundList.size() - 1) prettyString.append("§b}§f");
            else prettyString.append("§b}, §f");
        }
    }

    private static String evaluateSimpleCompoundToString(final NBTCompound compound, String key) {
        final NBTType type = compound.getType(key);

        return switch (type) {
            case NBTTagInt -> String.valueOf(compound.getInteger(key));
            case NBTTagLong -> compound.getLong(key) + "l§f";
            case NBTTagByte -> compound.getByte(key) + "b§f";
            case NBTTagFloat -> compound.getFloat(key) + "f§f";
            case NBTTagShort -> compound.getShort(key) + "s§f";
            case NBTTagDouble -> compound.getDouble(key) + "d§f";
            case NBTTagString -> "\"" + compound.getString(key) + "\"§f";
            default -> "§cSOMETHING WENT WRONG§f";
        };

    }



    ///////////////////////////////////////////////////////////////
    // NBTCompounds
    ///////////////////////////////////////////////////////////////

    public static NBTResult getCompoundFromPath(final NBTCompound compound, final String path) {
        if (path.isEmpty()) return new NBTResult(compound, path, getLastKey(path));

        // Returning a NBTResult, as the data could be in a NBTList instead
        return new NBTResult(recursiveCompoundFromPath(compound, path), path, getLastKey(path));
    }

    private static NBTCompound recursiveCompoundFromPath(final NBTCompound compound, final String path) {
        final String[] keys = pathToKeys(path);
        final String key = keys[0];
        final String newPath = removeFirstKey(path);
        boolean isFinalKey = (keys.length == 1);


        if (key.matches(".+\\[\\d]$")) {
            final NBTCompound newCompound = compound.getCompoundList(getArrayKeyValue(key))
                    .get(getIndexOfArrayKey(key));
            return recursiveCompoundFromPath(newCompound, newPath);
        }
        if (isFinalKey || isFullArray(key)) return compound;
        return recursiveCompoundFromPath(compound.getCompound(key), newPath);
    }

    // Items[0].tag.Items[..] -> {Items[0], tag, Items[..]}
    public static String[] pathToKeys(final String path) {
        return path.split("(?<!\\.)\\.(?!\\.)");
    }

    // Items[0].tag.Items[..] -> tag.Items[..]
    public static String removeFirstKey(final String path) {
        String[] out = path.split("\\.(?=\\w)", 2);
        return (out.length >= 2) ? out[1] : "";
    }

    // Items[0].tag.Items[..] -> Items[..]
    public static String getLastKey(final String path) {
        return path.replaceAll(".+\\.(?=\\w)", "");
    }

    // Items[0] -> Items
    public static String getArrayKeyValue(final String key) {
        return key.replaceAll("\\[\\d]$", "").replaceAll("\\[\\.{2}]$", "");
    }

    // Items[0] -> 0
    public static int getIndexOfArrayKey(final String key) {
        return Integer.parseInt(key.replaceAll(".+\\[|]$", ""));
    }

    // Items[0] -> true     id -> false
    public static boolean isArr(final String key) {
        return key.charAt(key.length() - 1) == ']';
    }

    // Items[..] -> true    Items[0] -> false
    public static boolean isFullArray(final String key) {
        return key.matches(".+\\.]$");
    }

    public static String pathToNonRegex(final String path) {
        return path.replaceAll("\\.", "\\\\.").replaceAll("\\[", "\\\\[");
    }

    public static Optional<ItemStack> getItemStack(final NBTCompound compound) {
        try {
            final ItemStack itemStack = NBTItem.convertNBTtoItem(compound);
            if (itemStack.getType() == Material.AIR) return Optional.empty();
            else return Optional.of(itemStack);

        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    public static List<String> getAllItemPaths(final NBTCompound compound) {
        return itemPathRecursion(compound, new LinkedList<>(), "");
    }

    private static List<String> itemPathRecursion(NBTCompound compound, List<String> paths, String currentPath) {
        for (String key : compound.getKeys()) {
            final NBTType type = compound.getType(key);
            final String newPath = (currentPath.isBlank()) ? key : String.format("%s.%s", currentPath, key);
            if (!(type == NBTType.NBTTagCompound || type == NBTType.NBTTagList)) continue;
            System.out.printf("Key: %s; Type: %s\n", key, type);

            if (type == NBTType.NBTTagCompound) { itemPathRecursionTag(compound.getCompound(key), paths, newPath); continue; }
            itemPathRecursionList(compound.getCompoundList(key), paths, newPath);
        }

        return paths;
    }

    private static void itemPathRecursionTag(NBTCompound compound, List<String> paths, String path) {
        if (getItemStack(compound).isPresent()) paths.add(path);
        itemPathRecursion(compound, paths, path);
    }

    private static void itemPathRecursionList(NBTCompoundList compoundList, List<String> paths, String path) {
        for (int i = 0; i < compoundList.size(); i++) {
            final NBTCompound nbt = compoundList.get(i);
            final String newPath = String.format("%s[%d]", path, i);

            if (getItemStack(nbt).isPresent()) paths.add(newPath);
            itemPathRecursion(nbt, paths, newPath);
        }
    }



    /*
     * This will represent the NBTCompound and the final key to access the data
     * */
    public static record NBTResult(NBTCompound compound, String path, String finalKey) { }

}
