package com.backpackmod;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.GameRules;

public final class BackpackLogic {
    public static ItemStack equipped(Player p) { return p.getAttachedOrElse(ModRegistry.EQUIPPED, ItemStack.EMPTY); }

    /** Equipped backpack first, then any backpacks in the inventory. */
    public static List<ItemStack> all(Player p) {
        List<ItemStack> out = new ArrayList<>();
        ItemStack e = equipped(p);
        if (e.getItem() instanceof BackpackItem) out.add(e);
        for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
            ItemStack s = p.getInventory().getItem(i);
            if (s.getItem() instanceof BackpackItem) out.add(s);
        }
        return out;
    }

    public static boolean holds(Player p, ItemStack s) {
        for (ItemStack o : all(p)) if (o == s) return true;
        return false;
    }

    public static boolean isOpen(Player p, ItemStack s) {
        return p.containerMenu instanceof BackpackMenu m && m.backpack() == s;
    }

    public static void openEquipped(ServerPlayer sp) {
        List<ItemStack> l = all(sp);
        if (!l.isEmpty()) BackpackItem.open(sp, l.get(0));
    }

    /** Magnet upgrade: every 2 ticks, pull items within 6 blocks straight into the backpack. */
    public static void tickMagnet(ServerPlayer p) {
        for (ItemStack pack : all(p)) {
            BackpackItem b = (BackpackItem) pack.getItem();
            if (!pack.getOrDefault(ModRegistry.UPGRADES, BackpackUpgrades.NONE).magnet() || isOpen(p, pack)) continue;
            BackpackContainer c = null;
            for (ItemEntity e : p.level().getEntitiesOfClass(ItemEntity.class, p.getBoundingBox().inflate(6),
                    ie -> ie.isAlive() && !ie.hasPickUpDelay() && ie.distanceToSqr(p) <= 36
                            && !(ie.getItem().getItem() instanceof BackpackItem))) {
                if (c == null) c = new BackpackContainer(pack, b.tier().slots());
                ItemStack st = e.getItem();
                int before = st.getCount();
                ItemStack rest = c.addItem(st.copy());
                int moved = before - rest.getCount();
                if (moved <= 0) continue;
                p.take(e, moved);
                if (rest.isEmpty()) e.discard(); else e.setItem(rest);
            }
        }
    }

    /** Auto-Feed upgrade: when hunger has dropped, eat the best-fitting plain food from the backpack. */
    public static void tickFeed(ServerPlayer p) {
        int level = p.getFoodData().getFoodLevel();
        if (level > 14) return;
        for (ItemStack pack : all(p)) {
            BackpackItem b = (BackpackItem) pack.getItem();
            if (!pack.getOrDefault(ModRegistry.UPGRADES, BackpackUpgrades.NONE).autoFeed() || isOpen(p, pack)) continue;
            BackpackContainer c = new BackpackContainer(pack, b.tier().slots());
            int bestSlot = -1, bestNutrition = 0;
            for (int i = 0; i < c.getContainerSize(); i++) {
                ItemStack s = c.getItem(i);
                FoodProperties f = s.get(DataComponents.FOOD);
                if (f == null || s.has(DataComponents.USE_REMAINDER)) continue;
                Consumable cons = s.get(DataComponents.CONSUMABLE);
                if (cons != null && !cons.onConsumeEffects().isEmpty()) continue; // skip rotten flesh, golden apples...
                if (f.nutrition() <= 20 - level && f.nutrition() > bestNutrition) { bestSlot = i; bestNutrition = f.nutrition(); }
            }
            if (bestSlot < 0) continue;
            ItemStack food = c.getItem(bestSlot);
            p.getFoodData().eat(food.get(DataComponents.FOOD));
            food.shrink(1);
            c.setItem(bestSlot, food.isEmpty() ? ItemStack.EMPTY : food);
            p.level().playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5f, 1.0f);
            return;
        }
    }

    /** Drop the equipped backpack on death unless keepInventory is on. */
    public static void dropOnDeath(ServerPlayer p) {
        if (p.level().getGameRules().get(GameRules.KEEP_INVENTORY)) return; // verify: gamerule API changed in 1.21.11+
        ItemStack s = equipped(p);
        if (s.isEmpty()) return;
        p.drop(s.copy(), true, false);
        p.setAttached(ModRegistry.EQUIPPED, ItemStack.EMPTY);
    }
    private BackpackLogic() {}
}
