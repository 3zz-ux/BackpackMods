package com.backpackmod.client;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Unit;

public class BackpackModel extends Model<Unit> {
    public BackpackModel(ModelPart root) { super(root, RenderTypes::entityCutout); }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // High-detail 3D backpack model designed to sit cleanly over armor without clipping
        root.addOrReplaceChild("bag", CubeListBuilder.create()
                // Main body: width 8, height 9.5, depth 4.5
                .texOffs(0, 0).addBox(-4.0f, 0.5f, 0.0f, 8.0f, 9.5f, 4.5f)
                // Top protective lid / flap
                .texOffs(0, 16).addBox(-4.1f, -0.5f, -0.2f, 8.2f, 2.0f, 4.8f)
                // Top carry handle
                .texOffs(0, 24).addBox(-1.5f, -2.0f, 1.5f, 3.0f, 1.5f, 1.0f)
                // Front 3D pouch
                .texOffs(28, 0).addBox(-3.0f, 4.0f, 4.5f, 6.0f, 5.5f, 1.5f)
                // Left vertical strap buckle
                .texOffs(0, 28).addBox(-2.5f, 2.5f, 4.3f, 1.2f, 2.0f, 0.4f)
                // Right vertical strap buckle
                .texOffs(6, 28).addBox(1.3f, 2.5f, 4.3f, 1.2f, 2.0f, 0.4f)
                // Pouch center brass clasp
                .texOffs(12, 28).addBox(-1.0f, 5.0f, 6.0f, 2.0f, 1.5f, 0.3f)
                // Left side utility pocket
                .texOffs(46, 0).addBox(-5.0f, 4.0f, 0.8f, 1.0f, 5.5f, 3.0f)
                // Right side utility pocket
                .texOffs(46, 10).addBox(4.0f, 4.0f, 0.8f, 1.0f, 5.5f, 3.0f)
                // Bottom rolled adventurer bedroll with straps
                .texOffs(28, 16).addBox(-4.5f, 10.0f, 0.5f, 9.0f, 2.5f, 3.5f),
                PartPose.offset(0.0f, 0.5f, 2.6f));

        return LayerDefinition.create(mesh, 64, 32);
    }
}
