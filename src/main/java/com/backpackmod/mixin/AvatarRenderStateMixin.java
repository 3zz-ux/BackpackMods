package com.backpackmod.mixin;

import com.backpackmod.client.BackpackStateAccess;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements BackpackStateAccess {
    @Unique private int backpackmod$tier = -1;
    @Override public int backpackmod$getTier() { return backpackmod$tier; }
    @Override public void backpackmod$setTier(int t) { backpackmod$tier = t; }
}
