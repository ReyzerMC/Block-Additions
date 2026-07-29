package org.reyzer.blockAdditions.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.reyzer.blockAdditions.BlockAdditions;
import org.reyzer.blockAdditions.blocks.UpgradedConduitBlock;
import org.reyzer.blockAdditions.entities.UpgradedConduitBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BlockAdditions.MOD_ID);

    public static final RegistryObject<Block> UPGRADED_CONDUIT = BLOCKS.register("upgraded_conduit",
            () -> new UpgradedConduitBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIAMOND)
                    .strength(3.0F)
                    .noOcclusion()));

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BlockAdditions.MOD_ID);

    public static final RegistryObject<BlockEntityType<UpgradedConduitBlockEntity>> UPGRADED_CONDUIT_BE = BLOCK_ENTITIES.register("upgraded_conduit",
            () -> BlockEntityType.Builder.of(UpgradedConduitBlockEntity::new, UPGRADED_CONDUIT.get()).build(null));
}
