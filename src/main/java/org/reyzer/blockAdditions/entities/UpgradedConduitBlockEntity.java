package org.reyzer.blockAdditions.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.reyzer.blockAdditions.init.ModBlockEntities;

import java.util.List;

public class UpgradedConduitBlockEntity extends BlockEntity {
    private boolean isActive = false;
    private int activeBlocksCount = 0;

    private static final List<BlockPos> STRUCTURE_OFFSETS = List.of(
            new BlockPos(-3, 3, 0), new BlockPos(3, 3, 0), new BlockPos(0, 3, -3), new BlockPos(0, 3, 3),
            new BlockPos(-3, 0, 0), new BlockPos(3, 0 ,0), new BlockPos(0, 0, -3), new BlockPos(0, 0, 3),
            new BlockPos(-3, -3, 0), new BlockPos(3, -3, 0), new BlockPos(0, -3, -3), new BlockPos(0, -3, 3)
    );

    public UpgradedConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.UPGRADED_CONDUIT_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, UpgradedConduitBlockEntity entity) {
        if (level == null) return;

        if (level.getGameTime() % 40 == 0) {
            entity.checkStructure(level, pos);
        }

        if (entity.isActive) {
            if (level.isClientSide) {
                entity.spawnCustomParticles(level, pos);
            }
            else {
                entity.applyEffectsToPlayer(level, pos);
            }
        }
    }

    private void checkStructure(Level level, BlockPos centerPos) {
        int validBlocks = 0;

        for (BlockPos offset : STRUCTURE_OFFSETS) {
            BlockPos targetPos = centerPos.offset(offset);

            if (level.getBlockState(targetPos).is(Blocks.NETHERITE_BLOCK)) {
                validBlocks++;
            }
        }

        this.activeBlocksCount = validBlocks;
        this.isActive = (validBlocks >= 8);
    }

    private void spawnCustomParticles(Level level, BlockPos pos) {
        RandomSource random = level.getRandom();
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;

        level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                x + (random.nextDouble() - 0.5D) * 0.5D,
                y + 0.5D + random.nextDouble() * 0.5D,
                z + (random.nextDouble() - 0.5D) * 0.5D,
                0.0D, 0.05D, 0.0D);

        if (random.nextInt(3) == 0 && !STRUCTURE_OFFSETS.isEmpty()) {
            BlockPos randomOffset = STRUCTURE_OFFSETS.get(random.nextInt(STRUCTURE_OFFSETS.size()));
            BlockPos sourcePos = pos.offset(randomOffset);

            double sx = sourcePos.getX() + 0.5D;
            double sy = sourcePos.getY() + 0.5D;
            double sz = sourcePos.getZ() + 0.5D;

            double dx = (x - sx) * 0.05D;
            double dy = (y - sy) * 0.05D;
            double dz = (z - sz) * 0.05D;

            level.addParticle(ParticleTypes.DRAGON_BREATH, sx, sy, sz, dx, dy, dz);
        }
    }

    private void applyEffectsToPlayer(Level level, BlockPos pos) {
        double range = 24.0D + (this.activeBlocksCount * 4.0D);
        AABB searchArea = new AABB(pos).inflate(range);

        List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, searchArea);

        for (Player player : nearbyPlayers) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 260, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 260, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 260, 0, true,false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, true, false));
        }
    }
}
