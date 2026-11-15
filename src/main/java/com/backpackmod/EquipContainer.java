package com.backpackmod;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** One-slot container backed by the player's synced "equipped backpack" attachment. */
public class EquipContainer implements Container {
    private final Player player;
    public EquipContainer(Player player) { this.player = player; }

    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return getItem(0).isEmpty(); }
    @Override public ItemStack getItem(int i) { return player.getAttachedOrElse(ModRegistry.EQUIPPED, ItemStack.EMPTY); }
    @Override public ItemStack removeItem(int i, int n) {
        ItemStack s = getItem(0);
        if (s.isEmpty()) return ItemStack.EMPTY;
        ItemStack out = s.split(n);
        if (s.isEmpty()) setItem(0, ItemStack.EMPTY);
        return out;
    }
    @Override public ItemStack removeItemNoUpdate(int i) {
        ItemStack s = getItem(0);
        setItem(0, ItemStack.EMPTY);
        return s;
    }
    @Override public void setItem(int i, ItemStack s) { player.setAttached(ModRegistry.EQUIPPED, s); }
    @Override public int getMaxStackSize() { return 1; }
    @Override public void setChanged() {}
    @Override public boolean stillValid(Player p) { return true; }
    @Override public void clearContent() { setItem(0, ItemStack.EMPTY); }
}
