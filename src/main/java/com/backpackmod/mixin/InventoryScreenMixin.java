package com.backpackmod.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {
    public InventoryScreenMixin(InventoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Inject(method = "extractBackground", at = @At("TAIL"))
    private void backpackmod$drawBackpackSlot(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        int x = this.leftPos + 76;
        int y = this.topPos + 43;

        // Standard Minecraft 18x18 slot frame bevel:
        // Top and Left dark bevel (0xFF373737)
        g.fill(x, y, x + 18, y + 1, 0xFF373737);
        g.fill(x, y, x + 1, y + 18, 0xFF373737);
        // Bottom and Right highlight bevel (0xFFFFFFFF)
        g.fill(x + 1, y + 17, x + 18, y + 18, 0xFFFFFFFF);
        g.fill(x + 17, y + 1, x + 18, y + 18, 0xFFFFFFFF);
        // Interior 16x16 slot fill (0xFF8B8B8B)
        g.fill(x + 1, y + 1, x + 17, y + 17, 0xFF8B8B8B);

        // If the backpack slot is empty, draw an elegant backpack placeholder silhouette (like the offhand shield icon)
        if (!this.menu.slots.isEmpty()) {
            Slot backpackSlot = this.menu.slots.get(this.menu.slots.size() - 1);
            if (backpackSlot.getItem().isEmpty()) {
                // Top handle
                g.fill(x + 7, y + 3, x + 11, y + 4, 0xFFA8A8A8);
                g.fill(x + 7, y + 4, x + 8, y + 5, 0xFFA8A8A8);
                g.fill(x + 10, y + 4, x + 11, y + 5, 0xFFA8A8A8);
                // Main body
                g.fill(x + 4, y + 5, x + 14, y + 14, 0xFF767676);
                // Top flap highlight
                g.fill(x + 5, y + 5, x + 13, y + 7, 0xFFA0A0A0);
                // Straps
                g.fill(x + 6, y + 7, x + 7, y + 13, 0xFF505050);
                g.fill(x + 11, y + 7, x + 12, y + 13, 0xFF505050);
                // Buckles
                g.fill(x + 6, y + 9, x + 7, y + 10, 0xFFC0C0C0);
                g.fill(x + 11, y + 9, x + 12, y + 10, 0xFFC0C0C0);
                // Front pouch
                g.fill(x + 7, y + 9, x + 11, y + 13, 0xFF626262);
            }
        }
    }
}
