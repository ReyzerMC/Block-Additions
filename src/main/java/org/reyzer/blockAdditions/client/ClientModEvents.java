package org.reyzer.blockAdditions.client;

import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.reyzer.blockAdditions.BlockAdditions;
import org.reyzer.blockAdditions.init.ModEntities;

@Mod.EventBusSubscriber(modid = BlockAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CRIMSON_MOON_FOLLOWER.get(), WitherSkeletonRenderer::new);
    }
}