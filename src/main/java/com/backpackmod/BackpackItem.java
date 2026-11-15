package com.backpackmod;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BackpackItem extends Item {
    private final BackpackTier tier;
    public BackpackItem(Properties p, BackpackTier tier) { super(p); this.tier = tier; }
    public BackpackTier tier() { return tier; }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer sp) open(sp, player.getItemInHand(hand));
        return InteractionResult.SUCCESS; // right-click only opens the GUI; never dumps items
    }

    public static void open(ServerPlayer sp, ItemStack stack) {
        if (!(stack.getItem() instanceof BackpackItem b)) return;
        sp.openMenu(new ExtendedScreenHandlerFactory<Integer>() {
            @Override public Integer getScreenOpeningData(ServerPlayer p) { return b.tier.ordinal(); }
            @Override public Component getDisplayName() { return stack.getHoverName(); }
            @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                return new BackpackMenu(id, inv, b.tier, new BackpackContainer(stack, b.tier.slots()), stack);
            }
        });
    }
}
