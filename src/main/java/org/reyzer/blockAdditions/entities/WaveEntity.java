package org.reyzer.blockAdditions.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WaveEntity extends Projectile {
    private int waveLevel = 1;

    private final List<UUID> hitEntities = new ArrayList<>();

    public WaveEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public int getWaveLevel() {
        return waveLevel;
    }

    public void setWaveLevel(int level) {
        this.waveLevel = level;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();

        Vec3 currentPos = this.position();
        Vec3 motion = this.getDeltaMovement();
        Vec3 nextPos = currentPos.add(motion);

        this.setPos(nextPos);

        // 1. ESTELA DE FUEGO AZUL (CLIENTE)
        if (this.level().isClientSide) {
            double scatter = 0.2D + (this.waveLevel * 0.1D);

            for (int i = 0; i < 3; i++) {
                double offsetX = (this.random.nextDouble() - 0.5D) * scatter;
                double offsetY = (this.random.nextDouble() - 0.5D) * 0.2D;
                double offsetZ = (this.random.nextDouble() - 0.5D) * scatter;

                this.level().addParticle(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        -motion.x * 0.1D,
                        0.02D,
                        -motion.z * 0.1D
                );
            }
        }

        // 2. COLISIONES Y DAÑO (SERVIDOR)
        if (!this.level().isClientSide) {

            // Impacto con bloques
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                spawnImpactParticles(this.position(), 25);
                this.discard();
                return;
            }

            // Impacto con entidades
            float radius = 0.8F + (this.waveLevel * 0.3F);
            AABB searchBox = this.getBoundingBox().inflate(radius, 0.5, radius);
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, searchBox, this::canHitEntity);

            for (LivingEntity target : targets) {
                if (!this.hitEntities.contains(target.getUUID())) {
                    float damageAmount = 3.0F + (this.waveLevel * 2.0F);

                    Entity owner = this.getOwner();
                    if (owner != null) {
                        target.hurt(this.damageSources().mobAttack((LivingEntity) owner), damageAmount);
                    } else {
                        target.hurt(this.damageSources().magic(), damageAmount);
                    }

                    target.knockback(0.4D, -motion.x, -motion.z);

                    // Partículas de impacto en el centro de la entidad alcanzada
                    Vec3 targetCenter = target.position().add(0, target.getBbHeight() / 2.0, 0);
                    spawnImpactParticles(targetCenter, 15);

                    this.hitEntities.add(target.getUUID());
                }
            }

            // Tiempo límite antes de disiparse
            if (this.tickCount > 25) {
                spawnImpactParticles(this.position(), 10);
                this.discard();
            }
        }
    }

    /**
     * Emite una ráfaga omnidireccional de partículas de fuego azul desde el servidor.
     */
    private void spawnImpactParticles(Vec3 point, int count) {
        if (this.level() instanceof ServerLevel serverLevel) {
            // Explosión de Fuego Azul
            serverLevel.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    point.x, point.y, point.z,
                    count,
                    0.3D, 0.3D, 0.3D,
                    0.15D
            );

            // Destellos de soporte (Glow)
            serverLevel.sendParticles(
                    ParticleTypes.GLOW,
                    point.x, point.y, point.z,
                    count / 2,
                    0.2D, 0.2D, 0.2D,
                    0.05D
            );
        }
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        Entity owner = this.getOwner();
        if (owner != null && entity.is(owner)) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.waveLevel = tag.getInt("WaveLevel");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("WaveLevel", this.waveLevel);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}