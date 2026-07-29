package org.reyzer.blockAdditions.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.reyzer.blockAdditions.BlockAdditions;
import org.reyzer.blockAdditions.bosses.BondOfLifeBoss;
import org.reyzer.blockAdditions.enchantments.BondOfLife;
import org.reyzer.blockAdditions.entities.Gamatoto;
import org.reyzer.blockAdditions.entities.WaveEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BlockAdditions.MOD_ID);

    public static final RegistryObject<EntityType<WaveEntity>> WAVE_PROJECTILE =
            ENTITIES.register("wave_projectile", () ->
                    EntityType.Builder.<WaveEntity>of(WaveEntity::new, MobCategory.MISC)
                            .sized(1.5f, 0.5f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("wave_projectile")
            );

    public static final RegistryObject<EntityType<BondOfLifeBoss>> CRIMSON_MOON_FOLLOWER =
            ENTITIES.register("crimson_moon_follower",
                    () -> EntityType.Builder.of(BondOfLifeBoss::new, MobCategory.MONSTER)
                            .sized(0.7F, 2.4F)
                            .build("crimson_moon_follower"));

    public static final RegistryObject<EntityType<Gamatoto>> GAMATOTO =
            ENTITIES.register("gamatoto",
                    () -> EntityType.Builder.of(Gamatoto::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F)
                            .build("gamatoto"));
}
