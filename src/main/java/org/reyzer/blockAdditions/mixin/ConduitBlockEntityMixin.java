package org.reyzer.blockAdditions.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
public abstract class ConduitBlockEntityMixin extends BlockEntity {

    @Shadow private boolean isActive;
    @Shadow private float activeRotation;

    public ConduitBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "serverTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void serverTick(Level level, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity, CallbackInfo ci) {
        ConduitBlockEntityMixin mixin = (ConduitBlockEntityMixin) (Object) blockEntity;

        if (blockAdditions$isUpgradedStructurePresent(level, pos)) {
            assert mixin != null;
            if (!mixin.isActive) {
                mixin.isActive = true;
                level.sendBlockUpdated(pos, state, state, 3);
            }

            if (level.getGameTime() % 20 == 0) {
                blockAdditions$applyUpgradedEffects(level, pos);
            }

            ci.cancel();
        }
    }

    @Inject(
            method = "clientTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void clientTick(Level level, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity, CallbackInfo ci) {
        ConduitBlockEntityMixin mixin = (ConduitBlockEntityMixin) (Object) blockEntity;

        if (blockAdditions$isUpgradedStructurePresent(level, pos)) {
            assert mixin != null;
            mixin.isActive = true;

            mixin.activeRotation += 2.50F;

            blockAdditions$spawnUpgradedParticles(level, pos);

            ci.cancel();
        }
    }

    @Unique
    private static boolean blockAdditions$isUpgradedStructurePresent(Level level, BlockPos pos) {
        BlockPos[] customOffsets = new BlockPos[] {
                new BlockPos(-3, 0, 0), new BlockPos(3, 0, 0),
                new BlockPos(0, -3, 0), new BlockPos(0, 3, 0),
                new BlockPos(0, 0, -3), new BlockPos(0, 0, 3)
        };

        int count = 0;
        for (BlockPos offset : customOffsets) {
            if (level.getBlockState(pos.offset(offset)).is(Blocks.NETHERITE_BLOCK)) {
                count++;
            }
        }

        return count >= 6;
    }

    @Unique
    private static void blockAdditions$applyUpgradedEffects(Level level, BlockPos pos) {
        double range = 48.0D;
        AABB area = new AABB(pos).inflate(range);
        List<Player> players = level.getEntitiesOfClass(Player.class, area);

        for (Player player : players) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 260, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 260, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 260, 0, true, false));
        }
    }

    @Unique
    private static void blockAdditions$spawnUpgradedParticles(Level level, BlockPos pos) {
        if (level.random.nextInt(3) == 0) {
            double x = pos.getX() + 0.5D + (level.random.nextDouble() - 0.5D);
            double y = pos.getY() + 0.5D + (level.random.nextDouble() - 0.5D);
            double z = pos.getZ() + 0.5D + (level.random.nextDouble() - 0.5D);

            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0.0, 0.05, 0.0);
        }
    }
}