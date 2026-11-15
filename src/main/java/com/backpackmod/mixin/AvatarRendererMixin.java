package com.backpackmod.mixin;

import com.backpackmod.BackpackItem;
import com.backpackmod.BackpackLogic;
import com.backpackmod.client.BackpackStateAccess;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Copies the worn backpack tier from the (synced) attachment into the render state. */
@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("TAIL"))
    private void backpackmod$tier(Avatar avatar, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        int tier = -1;
        if (avatar instanceof Player p) {
            ItemStack s = BackpackLogic.equipped(p);
            if (s.getItem() instanceof BackpackItem b) tier = b.tier().ordinal();
        }
        ((BackpackStateAccess) state).backpackmod$setTier(tier);
    }
}
