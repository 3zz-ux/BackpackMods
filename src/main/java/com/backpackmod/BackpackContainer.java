package com.backpackmod;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

/** A Container view over the DataComponents.CONTAINER component of a backpack stack (no NBT). */
public class BackpackContainer extends SimpleContainer {
    private final ItemStack backpack;
    private boolean loading = true;

    public BackpackContainer(ItemStack backpack, int size) {
        super(size);
        this.backpack = backpack;
        NonNullList<ItemStack> tmp = NonNullList.withSize(size, ItemStack.EMPTY);
        backpack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(tmp);
        for (int i = 0; i < size; i++) setItem(i, tmp.get(i));
        loading = false;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (loading) return;
        List<ItemStack> list = new ArrayList<>(getContainerSize());
        for (int i = 0; i < getContainerSize(); i++) list.add(getItem(i));
        backpack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));
    }
}
