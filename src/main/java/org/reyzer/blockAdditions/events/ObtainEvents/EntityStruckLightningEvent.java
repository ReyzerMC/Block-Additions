package org.reyzer.blockAdditions.events.ObtainEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.reyzer.blockAdditions.init.ModEnchantments;

public class EntityStruckLightningEvent {
    @SubscribeEvent
    public void onLightningStrike(EntityStruckByLightningEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        if (event.getLightning().getCause() != null) {

            ItemStack mainHand = event.getLightning().getCause().getMainHandItem();

            if (mainHand.is(Items.TRIDENT) && EnchantmentHelper.getTagEnchantmentLevel(Enchantments.CHANNELING, mainHand) > 0) {
                event.getEntity().getPersistentData().putBoolean("BlockAdditions_ThunderingMarked", true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide() && entity.getPersistentData().getBoolean("BlockAdditions_ThunderingMarked")) {
            if (entity.tickCount % 5 == 0) {
                ServerLevel level = (ServerLevel) entity.level();
                level.sendParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        entity.getX(), entity.getY() + (entity.getBbHeight() / 2), entity.getZ(),
                        5, 0.3, 0.3, 0.3, 0.05
                );
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (level.isClientSide()) return;

        if (entity.getPersistentData().getBoolean("BlockAdditions_ThunderingMarked")) {
            BlockPos entityPos = entity.blockPosition();

            for (BlockPos pos : BlockPos.betweenClosed(entityPos.offset(-5, -5, -5), entityPos.offset(5, 5, 5))) {
                if (level.getBlockState(pos).is(Blocks.LIGHTNING_ROD)) {
                    Block blockBelow = level.getBlockState(pos.below()).getBlock();

                    if (trySpawnThunderingBook((ServerLevel) level, pos, blockBelow)) {
                        break;
                    }
                }
            }
        }
    }

    private boolean trySpawnThunderingBook(ServerLevel level, BlockPos rodPos, Block blockBelow) {
        int chosenLevel = getChosenLevel(level, blockBelow);

        if (chosenLevel != -1) {
            ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(enchantedBook, new EnchantmentInstance(ModEnchantments.THUNDERING.get(), chosenLevel));


            double spawnX = rodPos.getX() + 0.5;
            double spawnY = rodPos.getY() + 1.2;
            double spawnZ = rodPos.getZ() + 0.5;

            ItemEntity itemEntity = new ItemEntity(level, spawnX, spawnY, spawnZ, enchantedBook);
            itemEntity.setDeltaMovement(0, 0, 0);

            level.addFreshEntity(itemEntity);

            level.sendParticles(ParticleTypes.END_ROD, spawnX, spawnY, spawnZ, 30, 0.2 ,0.2, 0.2, 0.05);
            level.sendParticles(ParticleTypes.FLASH, spawnX, spawnY, spawnZ, 1, 0, 0, 0, 0);

            return true;
        }

        return false;
    }

    private static int getChosenLevel(ServerLevel level, Block blockBelow) {
        RandomSource random = level.getRandom();
        int chosenLevel = -1;
        double roll = random.nextDouble() * 100;

        if (blockBelow == Blocks.IRON_BLOCK && roll < 15.0) {
            chosenLevel = 1;
        } else if (blockBelow == Blocks.GOLD_BLOCK && roll < 13.0) {  // 13% Nivel II
            chosenLevel = 2;
        } else if (blockBelow == Blocks.EMERALD_BLOCK && roll < 8.0) { // 8% Nivel III
            chosenLevel = 3;
        } else if (blockBelow == Blocks.DIAMOND_BLOCK && roll < 6.0) { // 6% Nivel IV
            chosenLevel = 4;
        } else if (blockBelow == Blocks.NETHERITE_BLOCK && roll < 3.0) { // 3% Nivel V
            chosenLevel = 5;
        }
        return chosenLevel;
    }
}
