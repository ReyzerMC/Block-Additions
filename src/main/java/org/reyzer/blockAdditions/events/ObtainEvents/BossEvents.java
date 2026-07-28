package org.reyzer.blockAdditions.events.ObtainEvents;

import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.reyzer.blockAdditions.BlockAdditions;
import org.reyzer.blockAdditions.bosses.BondOfLifeBoss;
import org.reyzer.blockAdditions.init.ModEntities;
import org.reyzer.blockAdditions.init.ModItems;

public class BossEvents {
    @Mod.EventBusSubscriber(modid = BlockAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.CRIMSON_MOON_FOLLOWER.get(), WitherSkeleton.createAttributes().build());
        }
    }

    @Mod.EventBusSubscriber(modid = BlockAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onBossDeath(LivingDeathEvent event) {
            if (event.getEntity() instanceof BondOfLifeBoss boss) {
                if (!boss.level().isClientSide()) {
                    ItemStack bloodCrystal = new ItemStack(ModItems.BLOOD_CRYSTAL.get(), 1);
                    boss.spawnAtLocation(bloodCrystal);
                }
            }
        }
    }
}
