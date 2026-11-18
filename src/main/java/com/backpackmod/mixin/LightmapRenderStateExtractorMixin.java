package com.backpackmod.mixin;

import com.backpackmod.client.BackpackClient;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ensures the lightmap shader applies full Night Vision brightness client-side
 * even when the player has no server-side potion effect or permissions.
 */
@Mixin(LightmapRenderStateExtractor.class)
public class LightmapRenderStateExtractorMixin {
    @Inject(method = "extract", at = @At("TAIL"))
    private void backpackmod$nightVision(LightmapRenderState state, float partialTick, CallbackInfo ci) {
        if (BackpackClient.isNightVisionActive()) {
            state.nightVisionEffectIntensity = 1.0F;
        }
    }
}
