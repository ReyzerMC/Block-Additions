package org.reyzer.blockAdditions.events.ObtainEvents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.reyzer.blockAdditions.BlockAdditions;
import org.reyzer.blockAdditions.init.ModEnchantments;

import java.util.*;

@Mod.EventBusSubscriber(modid = BlockAdditions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HotStuffMasonicRitual {
    private static final Map<BlockPos, RitualState> ACTIVE_RITUALS = new HashMap<>();

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();

        if (level.isClientSide()) return;

        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        ItemStack heldItem = event.getItemStack();

        if (event.getHand() != InteractionHand.MAIN_HAND || !heldItem.is(Items.BLAZE_ROD)) return;

        if (!isRitualStructureValid(level, pos)) return;

        if (ACTIVE_RITUALS.containsKey(pos)) return;

        ACTIVE_RITUALS.put(pos, new RitualState(player.getUUID()));
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 0.5F);
    }

    private static boolean isRitualStructureValid(Level level, BlockPos pos) {
        if (level.dimension() != Level.NETHER) return false;

        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.LAVA_CAULDRON)) return false;

        BlockPos diamondPos = pos.below();
        if (!level.getBlockState(diamondPos).is(Blocks.DIAMOND_BLOCK)) return false;

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (!level.getBlockState(diamondPos.relative(dir)).is(Blocks.MAGMA_BLOCK)) return false;
        }

        return true;
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide()) return;
        if (event.level.dimension() != Level.NETHER) return;

        ServerLevel level = (ServerLevel) event.level;
        if (ACTIVE_RITUALS.isEmpty()) return;

        List<BlockPos> toRemove = new ArrayList<>();

        for (Map.Entry<BlockPos, RitualState> entry : ACTIVE_RITUALS.entrySet()) {
            BlockPos pos = entry.getKey();
            RitualState state = entry.getValue();

            if (!level.getBlockState(pos).is(Blocks.LAVA_CAULDRON)) {
                toRemove.add(pos);
                continue;
            }

            state.ticksActive++;

            if (state.exploding) {
                processExplosionPhase(level, pos, state, toRemove);
                continue;
            }

            checkAndConsumeItems(level, pos, state);

            if (state.ticksActive % 10 == 0) {
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 0.8;
                double z = pos.getZ() + 0.5;

                level.sendParticles(ParticleTypes.FLAME, x, y, z, 12, 0.2, 0.2, 0.2, 0.02);
                level.sendParticles(ParticleTypes.LAVA, x, y, z, 4, 0.1, 0.1, 0.1, 0.0);

                AABB damageBox = new AABB(pos).inflate(2.0);
                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, damageBox)) {
                    entity.setRemainingFireTicks(40);
                    entity.hurt(level.damageSources().inFire(), 1.5F);
                }
            }
        }

        for (BlockPos pos : toRemove) {
            ACTIVE_RITUALS.remove(pos);
        }
    }

    private static void checkAndConsumeItems(ServerLevel level, BlockPos pos, RitualState state) {
        AABB cauldronBox = new AABB(
                pos.getX() - 0.2, pos.getY() + 0.4, pos.getZ() - 0.2,
                pos.getX() + 1.2, pos.getY() + 1.8, pos.getZ() + 1.2
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, cauldronBox);

        for (ItemEntity itemEntity : items) {
            if (!itemEntity.isAlive()) continue;

            ItemStack stack = itemEntity.getItem();

            // 1. Carga Ígnea
            if (state.step == 0 && stack.is(Items.FIRE_CHARGE)) {
                consumeOneItem(itemEntity, stack);
                state.step = 1;
                level.playSound(null, pos, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 1.0F, 0.8F);
                level.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.0);
                return;
            }

            // 2. Bloque de Netherita
            if (state.step == 1 && stack.is(Items.NETHERITE_BLOCK)) {
                consumeOneItem(itemEntity, stack);
                state.step = 2;
                level.playSound(null, pos, SoundEvents.NETHERITE_BLOCK_BREAK, SoundSource.BLOCKS, 1.0F, 0.5F);
                level.sendParticles(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 20, 0.3, 0.3, 0.3, 0.0);
                return;
            }

            // 3. Libro
            if (state.step == 2 && stack.is(Items.BOOK)) {
                consumeOneItem(itemEntity, stack);
                state.exploding = true;
                state.explosionTicks = 0;
                return;
            }
        }
    }

    private static void consumeOneItem(ItemEntity itemEntity, ItemStack stack) {
        if (stack.getCount() > 1) {
            stack.shrink(1);
        } else {
            itemEntity.discard();
        }
    }

    private static void processExplosionPhase(ServerLevel level, BlockPos pos, RitualState state, List<BlockPos> toRemove) {
        state.explosionTicks++;
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.8;
        double z = pos.getZ() + 0.5;

        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 20, 0.3, 0.4, 0.3, 0.05);
        level.sendParticles(ParticleTypes.SOUL, x, y, z, 5, 0.2, 0.2, 0.2, 0.02);

        if (state.explosionTicks % 10 == 0) {
            level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 0.5F + (state.explosionTicks * 0.02F));
        }

        if (state.explosionTicks >= 40) {
            toRemove.add(pos);

            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

            level.explode(null, x, y, z, 5.0F, Level.ExplosionInteraction.NONE);

            AABB boomBox = new AABB(pos).inflate(4.0);
            for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, boomBox)) {
                living.hurt(level.damageSources().generic(), 18.0F);
            }

            spawnHotStuffBook(level, pos);
        }
    }

    private static void spawnHotStuffBook(ServerLevel level, BlockPos pos) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);

        book.enchant(ModEnchantments.HOT_STUFF.get(), 1);

        ItemEntity droppedBook = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 1.2,
                pos.getZ() + 0.5,
                book
        );

        droppedBook.setInvulnerable(true);
        level.addFreshEntity(droppedBook);
    }

    private static class RitualState {
        final UUID playerUUID;
        int step = 0;
        int ticksActive = 0;
        boolean exploding = false;
        int explosionTicks = 0;

        RitualState(UUID playerUUID) {
            this.playerUUID = playerUUID;
        }
    }
}