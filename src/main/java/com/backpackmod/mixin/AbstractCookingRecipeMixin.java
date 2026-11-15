package com.backpackmod.mixin;

import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Furnace / blast furnace / smoker recipes cook in 5 ticks. Campfire recipes are left alone. */
@Mixin(AbstractCookingRecipe.class)
public abstract class AbstractCookingRecipeMixin {
    @Inject(method = "cookingTime", at = @At("HEAD"), cancellable = true)
    private void backpackmod$fast(CallbackInfoReturnable<Integer> cir) {
        if (!((Object) this instanceof CampfireCookingRecipe)) cir.setReturnValue(5);
    }
}
