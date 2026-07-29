package org.reyzer.blockAdditions;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.reyzer.blockAdditions.bosses.BondOfLifeBoss;
import org.reyzer.blockAdditions.events.*;
import org.reyzer.blockAdditions.events.ObtainEvents.BossEvents;
import org.reyzer.blockAdditions.events.ObtainEvents.DragonEggCraftingHandler;
import org.reyzer.blockAdditions.events.ObtainEvents.EntityStruckLightningEvent;
import org.reyzer.blockAdditions.events.ObtainEvents.HellForgedWitherEvent;
import org.reyzer.blockAdditions.init.ModEffects;
import org.reyzer.blockAdditions.init.ModEnchantments;
import org.reyzer.blockAdditions.init.ModEntities;
import org.reyzer.blockAdditions.init.ModItems;

@Mod(BlockAdditions.MOD_ID)
public class BlockAdditions {
    public static final String MOD_ID = "block_additions";

    public BlockAdditions(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModEnchantments.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        RegisterEvents(
                // EnchantmentEvents
                new ArrebatoEvent(),
                new AnvilUpdEvent(),
                new BondOfLifeEvent(),
                new ThunderingEvent(),
                new WaveEvent(),
                new TelekinesisEvent(),
                new HotStuffEvent(),
                new HellForgedEvent(),
                // ObtainEvents
                new EntityStruckLightningEvent(),
                new HellForgedWitherEvent(),
                new BossEvents(),
                new DragonEggCraftingHandler()
        );
    }

    public void RegisterEvents(Object... target) {
        for (Object listeners : target) {
            MinecraftForge.EVENT_BUS.register(listeners);
        }
    }
}
