package com.backpackmod.client;

import com.backpackmod.BackpackMod;
import com.backpackmod.ModRegistry;
import com.backpackmod.OpenBackpackPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BackpackClient implements ClientModInitializer {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(BackpackMod.id("backpack"), "main");
    private static KeyMapping openKey;

    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModRegistry.BACKPACK_MENU, BackpackScreen::new);
        ModelLayerRegistry.registerModelLayer(LAYER, BackpackModel::createLayer);

        openKey = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.backpackmod.open", InputConstants.KEY_B,
                KeyMapping.Category.register(BackpackMod.id("main"))));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.consumeClick()) if (mc.player != null) ClientPlayNetworking.send(new OpenBackpackPayload());
        });

        LivingEntityRenderLayerRegistrationCallback.EVENT.register((type, renderer, helper, ctx) -> {
            if (renderer instanceof AvatarRenderer)
                helper.register(new BackpackLayer((RenderLayerParent) renderer, new BackpackModel(ctx.bakeLayer(LAYER))));
        });
    }
}
