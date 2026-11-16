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

        // Main backpack body with side pockets, top handle, front pouch and buckle
        root.addOrReplaceChild("bag", CubeListBuilder.create()
                // Main body
                .texOffs(0, 0).addBox(-4.5f, 0f, 0f, 9f, 10f, 4.5f)
                // Front pouch
                .texOffs(28, 0).addBox(-3.5f, 3.5f, 4.5f, 7f, 5.5f, 1.5f)
                // Front buckle / clasp
                .texOffs(0, 20).addBox(-1f, 4.5f, 5.5f, 2f, 2f, 0.5f)
                // Top lid / cover flap
                .texOffs(0, 15).addBox(-4.5f, -1.5f, -0.5f, 9f, 2f, 5f)
                // Top carry handle
                .texOffs(28, 8).addBox(-2f, -3f, 1f, 4f, 2f, 1f)
                // Left side utility pocket
                .texOffs(46, 0).addBox(-5.5f, 3f, 0.5f, 1f, 5.5f, 3f)
                // Right side utility pocket
                .texOffs(46, 9).addBox(4.5f, 3f, 0.5f, 1f, 5.5f, 3f),
                PartPose.offset(0f, 1f, 2f));

        return LayerDefinition.create(mesh, 64, 32);
    }
}
