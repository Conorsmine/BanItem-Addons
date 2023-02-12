package com.conorsmine.net.banbt.mojangson;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
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
        itemCompound.mergeCompound(rec);
        return this;
    }

    // Very important note, because this could have saved me some time:
    // The NBTCompound#addCompound() method returns the "inside" of the
    // added compound. But it still modifies the original to have the
    // compound added.
    private void addCompounds(NBTCompound compound, String nbtPath, Object o) {
        String newPath = MojangsonUtils.removeLastKey(nbtPath);
        if (StringUtils.isBlank(newPath)) { addDataToCompound(compound, nbtPath, o); return; }

        String[] compArr = MojangsonUtils.pathToKeys(newPath);
        Iterator<String> it = Arrays.stream(compArr).iterator();
        while (it.hasNext()){
            compound = compound.addCompound(it.next());

            if (!it.hasNext()) {
                String dataKey = MojangsonUtils.getLastKey(nbtPath);
                addDataToCompound(compound, dataKey, o);
            }
        }
    }

    private void addDataToCompound(NBTCompound compound, String key, Object o) {
        MojangsonUtils.setSimpleDataFromKey(compound, key, o);
    }

    public NBTCompound getItemCompound() {
        System.out.println(itemCompound);
        return itemCompound;
    }
}
