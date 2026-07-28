package org.reyzer.blockAdditions.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.reyzer.blockAdditions.BlockAdditions;
import org.reyzer.blockAdditions.entityModels.WaveModel;
import org.reyzer.blockAdditions.init.ModEntities;
import org.reyzer.blockAdditions.models.WaveRenderer;

@Mod.EventBusSubscriber(modid = BlockAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.WAVE_PROJECTILE.get(), WaveRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WaveModel.LAYER_LOCATION, WaveModel::createBodyLayer);
    }
}
