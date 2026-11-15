package com.backpackmod.mixin;

import com.backpackmod.BackpackSlot;
import com.backpackmod.EquipContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Adds the backpack slot above the offhand slot (appended last, so vanilla slot indices are unchanged). */
@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends AbstractContainerMenu {
    protected InventoryMenuMixin(MenuType<?> type, int id) { super(type, id); }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void backpackmod$addSlot(Inventory inv, boolean active, Player owner, CallbackInfo ci) {
        this.addSlot(new BackpackSlot(new EquipContainer(owner), 77, 44));
    }
}
