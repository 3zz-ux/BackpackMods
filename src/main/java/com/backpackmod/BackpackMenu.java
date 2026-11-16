package com.backpackmod;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BackpackMenu extends AbstractContainerMenu {
    private final BackpackTier tier;
    private final Container container;
    private final ItemStack backpack;

    /** Client-side constructor (tier synced via ExtendedScreenHandlerType). */
    public BackpackMenu(int id, Inventory inv, int tierOrdinal) {
        this(id, inv, BackpackTier.byOrdinal(tierOrdinal),
                new SimpleContainer(BackpackTier.byOrdinal(tierOrdinal).slots()), ItemStack.EMPTY);
    }

    public BackpackMenu(int id, Inventory inv, BackpackTier tier, Container container, ItemStack backpack) {
        super(ModRegistry.BACKPACK_MENU, id);
        this.tier = tier; this.container = container; this.backpack = backpack;
        int w = width(tier);
        int gx = (w - tier.cols * 18) / 2 + 1, gy = 18;
        for (int r = 0; r < tier.rows; r++)
            for (int c = 0; c < tier.cols; c++)
                addSlot(new Slot(container, r * tier.cols + c, gx + c * 18, gy + r * 18) {
                    @Override public boolean mayPlace(ItemStack s) { return !(s.getItem() instanceof BackpackItem); }
                });
        int ix = (w - 162) / 2 + 1, iy = gy + tier.rows * 18 + 14;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++) addSlot(new Slot(inv, c + r * 9 + 9, ix + c * 18, iy + r * 18));
        for (int c = 0; c < 9; c++) addSlot(new Slot(inv, c, ix + c * 18, iy + 58));
    }

    public static int width(BackpackTier t) { return Math.max(t.cols, 9) * 18 + 14; }
    public static int height(BackpackTier t) { return 18 + t.rows * 18 + 14 + 58 + 18 + 7; }
    public BackpackTier tier() { return tier; }
    public ItemStack backpack() { return backpack; }

    @Override
    public void clicked(int slotId, int button, ContainerInput type, Player player) {
        if (!backpack.isEmpty()) { // can't move the backpack that is currently open
            if (slotId >= 0 && slotId < slots.size() && slots.get(slotId).getItem() == backpack) return;
            if (type == ContainerInput.SWAP && button >= 0 && button < 9 && player.getInventory().getItem(button) == backpack) return;
        }
        super.clicked(slotId, button, type, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), copy = stack.copy();
        int n = tier.slots();
        if (index < n) { if (!moveItemStackTo(stack, n, slots.size(), true)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(stack, 0, n, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }

    @Override public boolean stillValid(Player p) { return backpack.isEmpty() || BackpackLogic.holds(p, backpack); }
}
