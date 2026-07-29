package org.reyzer.blockAdditions.events.attrEvents;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.reyzer.blockAdditions.BlockAdditions;
import org.reyzer.blockAdditions.entities.Gamatoto;
import org.reyzer.blockAdditions.init.ModEntities;

@Mod.EventBusSubscriber(modid = BlockAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommonEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.GAMATOTO.get(), Gamatoto.createMobAttributes().build());
    }
}
