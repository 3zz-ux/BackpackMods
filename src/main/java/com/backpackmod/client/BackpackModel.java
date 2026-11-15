package com.backpackmod.client;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Unit;

public class BackpackModel extends Model<Unit> {
    public BackpackModel(ModelPart root) { super(root, RenderType::entityCutoutNoCull); }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("bag", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4f, 0f, 0f, 8f, 10f, 4f)      // main bag
                .texOffs(24, 0).addBox(-3f, 4f, 4f, 6f, 4f, 1f)      // front pocket
                .texOffs(0, 16).addBox(-3.5f, -1f, 0.5f, 7f, 1f, 3f), // top flap
                PartPose.offset(0f, 1f, 2f));
        return LayerDefinition.create(mesh, 64, 32);
    }
}
