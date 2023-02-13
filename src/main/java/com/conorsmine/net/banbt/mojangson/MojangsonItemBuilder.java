package com.conorsmine.net.banbt.mojangson;

import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import net.minecraft.nbt.NBTTagList;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MojangsonItemBuilder {

    private final NBTCompound itemCompound;

    public MojangsonItemBuilder(@Nonnull ItemStack item) {
        this.itemCompound = NBTItem.convertItemtoNBT(item);
    }

    public MojangsonItemBuilder() {
        this.itemCompound = new NBTContainer();
        setDefault();
    }

    private void setDefault() {
        itemCompound.setString("id", "minecraft:air");
        itemCompound.setByte("Count", (byte) 1);
        itemCompound.setShort("Damage", (short) 0);
    }

    /**
     * This method adds the specified data following the path.
     * @param nbtPath Path to the data
     * @param o Data to be set
     */
    public MojangsonItemBuilder addData(@Nonnull final String nbtPath, @Nonnull final Object o) {
        NBTCompound rec = new NBTContainer();
        addCompounds(rec, nbtPath, o);
        System.out.println("§a" + itemCompound);
        itemCompound.mergeCompound(rec);
        System.out.println("§c" + itemCompound);
        return this;
    }

    private void addCompounds(NBTCompound compound, String nbtPath, Object o) {
        String newPath = MojangsonUtils.removeLastKey(nbtPath);
        if (StringUtils.isBlank(newPath)) { addDataToCompound(compound, nbtPath, o); return; }

        String[] compArr = MojangsonUtils.pathToKeys(newPath);
        Iterator<String> it = Arrays.stream(compArr).iterator();
        while (it.hasNext()) {
            final String key = it.next();
            if (MojangsonUtils.isArr(key)) { compound = addList(compound, key); }
            else { compound = compound.addCompound(key); }

            if (!it.hasNext()) addDataToCompound(compound, MojangsonUtils.getLastKey(nbtPath), o);
        }
    }

    private NBTCompound addList(NBTCompound compound, String key) {
        if (MojangsonUtils.isFullArray(key))
            throw new UnsupportedOperationException("Full arrays are not supported: \"%s\"");

        final NBTCompoundList list = addNBTTagList(compound, MojangsonUtils.getArrayKeyValue(key));
        int index = MojangsonUtils.getIndexOfArrayKey(key);
        int missing = Math.max(0, ((index + 1) - list.size())); // How many indexes are missing
        addMissingIndexes(list, missing);
        return list.get(index);
    }

    private void addDataToCompound(NBTCompound compound, String key, Object o) {
        MojangsonUtils.setSimpleDataFromKey(compound, key, o);
    }

    private NBTCompoundList addNBTTagList(NBTCompound compound, String key) {
        NBTCompoundList list = compound.getCompoundList(key);
        if (list.size() > 0) return list;

        // For some reason, the list on "generates" if a value
        // was added. So I add and remove a value to ensure that the list
        // has been created.
        ((List<ReadWriteNBT>) list).add(new NBTContainer());
        list.remove(list.size() - 1);
        return list;
    }

    private void addMissingIndexes(NBTCompoundList list, int missing) {
        for (int i = 0; i < missing; i++)
            ((List<ReadWriteNBT>) list).add(new NBTContainer());
    }

    public NBTCompound getItemCompound() {
        System.out.println(itemCompound);
        return itemCompound;
    }
}
