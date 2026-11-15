package com.backpackmod;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Backpack + 8 next-tier material  -> next tier (ItemStack#transmuteCopy keeps CONTAINER + UPGRADES components)
 * Backpack + Ender Eye             -> Magnet upgrade
 * Backpack + Golden Carrot         -> Auto-Feed upgrade
 */
public class UpgradeRecipe extends CustomRecipe {
    @Override public boolean matches(CraftingInput in, Level level) { return result(in) != null; }
    @Override public ItemStack assemble(CraftingInput in) { ItemStack r = result(in); return r == null ? ItemStack.EMPTY : r; }
    @Override public RecipeSerializer<? extends CustomRecipe> getSerializer() { return ModRegistry.UPGRADE_RECIPE; }

    private static ItemStack result(CraftingInput in) {
        ItemStack pack = ItemStack.EMPTY;
        List<ItemStack> others = new ArrayList<>();
        for (ItemStack s : in.items()) {
            if (s.isEmpty()) continue;
            if (s.getItem() instanceof BackpackItem) { if (!pack.isEmpty()) return null; pack = s; }
            else others.add(s);
        }
        if (pack.isEmpty() || others.isEmpty()) return null;
        BackpackItem b = (BackpackItem) pack.getItem();
        BackpackUpgrades up = pack.getOrDefault(ModRegistry.UPGRADES, BackpackUpgrades.NONE);
        if (others.size() == 1) {
            Item o = others.get(0).getItem();
            if (o == Items.ENDER_EYE && !up.magnet()) { ItemStack r = pack.copy(); r.set(ModRegistry.UPGRADES, up.withMagnet()); return r; }
            if (o == Items.GOLDEN_CARROT && !up.autoFeed()) { ItemStack r = pack.copy(); r.set(ModRegistry.UPGRADES, up.withAutoFeed()); return r; }
            return null;
        }
        BackpackTier next = b.tier().next();
        if (next != null && others.size() == 8 && others.stream().allMatch(s -> s.is(next.upgradeMaterial)))
            return pack.transmuteCopy(ModRegistry.BACKPACKS.get(next));
        return null;
    }
}
