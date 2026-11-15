package com.backpackmod;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public enum BackpackTier {
    LEATHER("leather", 9, 3, false, null),
    IRON("iron", 9, 6, false, Items.IRON_INGOT),
    GOLDEN("golden", 9, 9, false, Items.GOLD_INGOT),
    DIAMOND("diamond", 12, 9, false, Items.DIAMOND),
    NETHERITE("netherite", 13, 9, true, Items.NETHERITE_INGOT);

    public final String id;
    public final int cols, rows;
    public final boolean fireproof;
    /** 8 of these around the previous-tier backpack craft this tier. */
    public final Item upgradeMaterial;

    BackpackTier(String id, int cols, int rows, boolean fireproof, Item mat) {
        this.id = id; this.cols = cols; this.rows = rows; this.fireproof = fireproof; this.upgradeMaterial = mat;
    }
    public int slots() { return cols * rows; }
    public BackpackTier next() { return ordinal() + 1 < values().length ? values()[ordinal() + 1] : null; }
    public static BackpackTier byOrdinal(int i) { return values()[Math.max(0, Math.min(i, values().length - 1))]; }
}
