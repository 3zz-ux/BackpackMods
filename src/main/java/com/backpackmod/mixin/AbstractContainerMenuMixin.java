package com.backpackmod.mixin;

import com.backpackmod.BackpackItem;
import com.backpackmod.BackpackMenu;
import com.backpackmod.EquipContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Right-clicking a backpack in the inventory opens it instead of splitting/picking up the stack. */
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
    @Shadow @Final public NonNullList<Slot> slots;
    @Shadow public abstract ItemStack getCarried();

    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void backpackmod$rightClick(int slotId, int button, ContainerInput type, Player player, CallbackInfo ci) {
        if (type != ContainerInput.PICKUP || button != 1 || slotId < 0 || slotId >= slots.size()) return;
        if (!getCarried().isEmpty() || (Object) this instanceof BackpackMenu) return;
        Slot slot = slots.get(slotId);
        if (!(slot.container instanceof Inventory || slot.container instanceof EquipContainer)) return;
        if (!(slot.getItem().getItem() instanceof BackpackItem)) return;
        ci.cancel(); // cancel on both sides: no split / no drop
        if (player instanceof ServerPlayer sp) BackpackItem.open(sp, slot.getItem());
    }
}
