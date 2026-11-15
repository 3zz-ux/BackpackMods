package com.backpackmod.client;

import com.backpackmod.BackpackMod;
import com.backpackmod.BackpackTier;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

/** Draws the per-tier 3D backpack on the player's back via the submit/collector pipeline (Blaze3D, no raw GL). */
public class BackpackLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final Identifier[] TEXTURES = new Identifier[BackpackTier.values().length];
    static { for (BackpackTier t : BackpackTier.values()) TEXTURES[t.ordinal()] = BackpackMod.id("textures/entity/backpack_" + t.id + ".png"); }

    private final BackpackModel model;

    public BackpackLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent, BackpackModel model) {
        super(parent);
        this.model = model;
    }

    @Override
    public void submit(PoseStack pose, SubmitNodeCollector collector, int light, AvatarRenderState state, float yRot, float xRot) {
        int tier = ((BackpackStateAccess) state).backpackmod$getTier();
        if (tier < 0 || state.isInvisible) return;
        pose.pushPose();
        getParentModel().body.translateAndRotate(pose); // follows sneaking / body rotation
        collector.submitModel(model, Unit.INSTANCE, pose, model.renderType(TEXTURES[tier]), light,
                OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        pose.popPose();
    }
}
