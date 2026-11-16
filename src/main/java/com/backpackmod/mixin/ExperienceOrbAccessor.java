package com.backpackmod.mixin;

import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** The orb value now lives in synched entity data; the private setValue(int) writes it. */
@Mixin(ExperienceOrb.class)
public interface ExperienceOrbAccessor {
    @Invoker("setValue") void backpackmod$setValue(int value);
}
