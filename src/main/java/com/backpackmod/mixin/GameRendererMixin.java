package com.backpackmod.mixin;

import com.backpackmod.client.BackpackClient;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Provides 100% client-side Night Vision brightness without requiring OP or server permissions.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "nightVisionScale", at = @At("HEAD"), cancellable = true)
    private static void backpackmod$nightVisionScale(LivingEntity entity, float partialTick, CallbackInfoReturnable<Float> cir) {
        if (BackpackClient.isNightVisionActive()) {
            cir.setReturnValue(1.0F);
        }
    }
}
