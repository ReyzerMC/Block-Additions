package org.reyzer.blockAdditions.entityModels;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.reyzer.blockAdditions.BlockAdditions;

public class WaveModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(BlockAdditions.MOD_ID, "wavemodel"), "main");
    public final ModelPart bb_main;

    public WaveModel(ModelPart root) {
        this.bb_main = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 1.0F, -7.0F, 2.0F, 0.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 4).addBox(-2.0F, 0.0F, -7.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(10, 13).addBox(-3.0F, 0.0F, -6.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(6, 14).addBox(-3.0F, -1.0F, -6.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(10, 16).addBox(3.0F, -1.0F, -6.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 14).addBox(3.0F, -2.0F, -6.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 17).addBox(-3.0F, -2.0F, -6.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(2.0F, 0.0F, -6.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 9).addBox(1.0F, 0.0F, -7.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(10, 4).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(10, 7).addBox(-1.0F, 0.0F, -9.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(10, 10).addBox(-1.0F, -1.0F, -10.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 3).addBox(-1.0F, -2.0F, -10.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(16, 13).addBox(-1.0F, -3.0F, -10.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(6, 18).addBox(0.0F, -4.0F, -10.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(18, 6).addBox(0.0F, -4.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(14, 16).addBox(-1.0F, -3.0F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(14, 17).addBox(2.0F, -1.0F, -4.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(16, 17).addBox(2.0F, -1.0F, -6.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(18, 4).addBox(2.0F, -2.0F, -6.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(18, 5).addBox(-3.0F, -2.0F, -6.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(2, 18).addBox(-3.0F, -1.0F, -4.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(4, 18).addBox(-3.0F, -1.0F, -6.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(16, 3).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 0).addBox(-1.0F, -1.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 24.0F, 1.0F, 0.0F, 1.5708F, 3.1416F));

        PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(8, 17).addBox(1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -1.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r2 = bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(6, 17).addBox(1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r3 = bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(4, 17).addBox(1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -1.0F, -8.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r4 = bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(2, 17).addBox(1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -8.0F, 0.0F, 1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}