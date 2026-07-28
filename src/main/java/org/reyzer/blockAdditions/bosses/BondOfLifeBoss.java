package org.reyzer.blockAdditions.bosses;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.reyzer.blockAdditions.init.ModEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BondOfLifeBoss extends WitherSkeleton {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.literal("§c§lCrimson Moon Follower"),
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private boolean phase75Triggered = false;
    private boolean phase50Triggered = false;
    private boolean phase25Triggered = false;

    private final List<WitherSkeleton> activeMinions = new ArrayList<>();
    private boolean isInvulnerablePhase = false;
    private int teleportCooldown = 0;
    private int rangedAttackCooldown = 0; // Cooldown interno para disparar calaveras

    public BondOfLifeBoss(EntityType<? extends WitherSkeleton> type, Level level) {
        super(type, level);

        if (this.getAttribute(Attributes.MAX_HEALTH) != null) {
            Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(600.0D);
            this.setHealth(600.0F);
        }
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(12.0D);
        }
        if (this.getAttribute(Attributes.ARMOR) != null) {
            Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(20.0D);
        }
        if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.35D); // Velocidad adecuada de persecución
        }
        if (this.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
            Objects.requireNonNull(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(1.0D);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            activeMinions.removeIf(minion -> !minion.isAlive());

            if (isInvulnerablePhase) {
                // Durante la fase de esbirros se queda inmóvil defendiendo
                this.getNavigation().stop();

                if (activeMinions.isEmpty()) {
                    isInvulnerablePhase = false;
                    this.bossEvent.setColor(BossEvent.BossBarColor.RED);
                }
            }
        }
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        if (this.level().isClientSide()) return;

        // Comprobación de cambio de fases
        float healthRatio = this.getHealth() / this.getMaxHealth();

        if (!phase75Triggered && healthRatio <= 0.75F) {
            phase75Triggered = true;
            spawnMinionPhase(2);
        } else if (!phase50Triggered && healthRatio <= 0.50F) {
            phase50Triggered = true;
            spawnMinionPhase(3);
        } else if (!phase25Triggered && healthRatio <= 0.25F) {
            phase25Triggered = true;
            spawnMinionPhase(4);
        }

        if (isInvulnerablePhase) return;

        if (teleportCooldown > 0) teleportCooldown--;
        if (rangedAttackCooldown > 0) rangedAttackCooldown--;

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            double distanceSq = this.distanceToSqr(target);

            // 1. Teletransporte si está muy lejos (> 10 bloques)
            if (distanceSq > 100.0D && teleportCooldown <= 0) {
                teleportTowardsTarget(target);
                teleportCooldown = 200; // 10 segundos
            }
            // 2. Ataque a distancia (Calavera Wither) cada 2.5 segundos (50 ticks) si está entre 4 y 20 bloques
            else if (distanceSq >= 16.0D && distanceSq <= 400.0D && rangedAttackCooldown <= 0) {
                shootWitherSkull(target);
                rangedAttackCooldown = 50;
            }
        }

        // Habilidad de Robo de Vida (Siphon) por debajo del 50% de HP
        if (this.getHealth() < this.getMaxHealth() * 0.5F && this.tickCount % 100 == 0) {
            AABB area = this.getBoundingBox().inflate(8.0D);
            List<LivingEntity> nearby = this.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this);

            for (LivingEntity entity : nearby) {
                entity.hurt(this.damageSources().magic(), 4.0F);
                this.heal(4.0F);
            }
        }
    }

    private void shootWitherSkull(LivingEntity target) {
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.5D) - this.getY(0.5D);
        double d2 = target.getZ() - this.getZ();

        WitherSkull skull = new WitherSkull(this.level(), this, d0, d1, d2);
        skull.setPos(this.getX(), this.getEyeY(), this.getZ());
        this.level().addFreshEntity(skull);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isInvulnerablePhase) {
            return false;
        }
        return super.hurt(source, amount);
    }

    private void spawnMinionPhase(int minionCount) {
        this.isInvulnerablePhase = true;
        this.bossEvent.setColor(BossEvent.BossBarColor.WHITE);

        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < minionCount; i++) {
                WitherSkeleton minion = EntityType.WITHER_SKELETON.create(serverLevel);
                if (minion != null) {
                    double offsetX = (this.random.nextDouble() - 0.5D) * 4.0D;
                    double offsetZ = (this.random.nextDouble() - 0.5D) * 4.0D;
                    minion.moveTo(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ, this.getYRot(), 0.0F);

                    if (minion.getAttribute(Attributes.MAX_HEALTH) != null) {
                        Objects.requireNonNull(minion.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(50.0D);
                        minion.setHealth(50.0F);
                    }

                    equipMinionItem(minion, EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET), Enchantments.ALL_DAMAGE_PROTECTION, 4);
                    equipMinionItem(minion, EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE), Enchantments.ALL_DAMAGE_PROTECTION, 4);
                    equipMinionItem(minion, EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS), Enchantments.ALL_DAMAGE_PROTECTION, 4);
                    equipMinionItem(minion, EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS), Enchantments.ALL_DAMAGE_PROTECTION, 4);
                    equipMinionItem(minion, EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD), Enchantments.SHARPNESS, 5);

                    if (this.getTarget() != null) {
                        minion.setTarget(this.getTarget());
                    }

                    serverLevel.addFreshEntity(minion);
                    this.activeMinions.add(minion);
                }
            }
        }
    }

    private void equipMinionItem(WitherSkeleton minion, EquipmentSlot slot, ItemStack item, Enchantment ench, int level) {
        item.enchant(ench, level);
        item.getOrCreateTag().putBoolean("Unbreakable", true);
        minion.setItemSlot(slot, item);
        minion.setDropChance(slot, 0.0F);
    }

    private void teleportTowardsTarget(LivingEntity target) {
        double targetX = target.getX() + (this.random.nextDouble() - 0.5D) * 4.0D;
        double targetY = target.getY();
        double targetZ = target.getZ() + (this.random.nextDouble() - 0.5D) * 4.0D;

        this.level().broadcastEntityEvent(this, (byte) 46);
        this.teleportTo(targetX, targetY, targetZ);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
        sword.enchant(ModEnchantments.BOND_OF_LIFE.get(), 3);

        this.setItemSlot(EquipmentSlot.MAINHAND, sword);
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));

        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setDropChance(EquipmentSlot.CHEST, 0.0F);
    }

    @Override
    protected void registerGoals() {
        // Metas de comportamiento
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false)); // Persigue activamente y ataca con la espada
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        // Selección de objetivo
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
}
