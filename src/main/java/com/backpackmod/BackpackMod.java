package com.backpackmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.CreativeModeTabs;

public class BackpackMod implements ModInitializer {
    public static final String MOD_ID = "backpackmod";
    private static final String XP_TAG = "backpackmod_x6";

    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(MOD_ID, path); }

    @Override
    public void onInitialize() {
        ModRegistry.init();

        PayloadTypeRegistry.playC2S().register(OpenBackpackPayload.TYPE, OpenBackpackPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OpenBackpackPayload.TYPE,
                (payload, ctx) -> BackpackLogic.openEquipped(ctx.player()));

        PlayerBlockBreakEvents.BEFORE.register(BreakActions::before);
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayer sp) BackpackLogic.dropOnDeath(sp);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int t = server.getTickCount();
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                if (t % 2 == 0) BackpackLogic.tickMagnet(p);
                if (t % 20 == 0) BackpackLogic.tickFeed(p);
            }
        });

        // 6x XP: every orb is multiplied exactly once (tagged so reloads/merges don't re-multiply).
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof ExperienceOrb orb && !orb.getTags().contains(XP_TAG)) {
                orb.addTag(XP_TAG);
                ((com.backpackmod.mixin.ExperienceOrbAccessor) orb).backpackmod$setValue(orb.getValue() * 6);
            }
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(e -> {
            ModRegistry.BACKPACKS.values().forEach(e::accept);
            ModRegistry.HAMMERS.forEach(e::accept);
        });
    }
}
