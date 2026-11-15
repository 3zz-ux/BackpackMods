package com.backpackmod;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** The dedicated equipment slot shown in the player inventory screen (above the offhand). */
public class BackpackSlot extends Slot {
    public BackpackSlot(Container c, int x, int y) {
        super(c, 0, x, y);
        setBackground(BackpackMod.id("container/slot_backpack"));
    }
    @Override public boolean mayPlace(ItemStack s) { return s.getItem() instanceof BackpackItem; }
    @Override public int getMaxStackSize() { return 1; }
}
