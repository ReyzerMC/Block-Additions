package org.reyzer.blockAdditions.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.reyzer.blockAdditions.BlockAdditions;
import org.reyzer.blockAdditions.entities.WaveEntity;
import org.reyzer.blockAdditions.entityModels.WaveModel;

public class WaveRenderer extends EntityRenderer<WaveEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(BlockAdditions.MOD_ID, "textures/entity/wave.png");
    private final WaveModel<WaveEntity> model;

    public WaveRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new WaveModel<WaveEntity>(context.bakeLayer(WaveModel.LAYER_LOCATION));
    }

    @Override
    public void render(WaveEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));

        float scale = 1.0f + (entity.getWaveLevel() * 0.3f);
        poseStack.scale(scale, scale, scale);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull WaveEntity entity) {
        return TEXTURE;
    }
}
