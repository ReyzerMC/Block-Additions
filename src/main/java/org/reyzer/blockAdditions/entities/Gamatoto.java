package org.reyzer.blockAdditions.entities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.reyzer.blockAdditions.init.ModEnchantments;

import javax.annotation.Nullable;
import java.util.Objects;

public class Gamatoto extends WanderingTrader {

    public Gamatoto(EntityType<? extends WanderingTrader> type, Level level) {
        super(type, level);
        this.setCustomName(Component.translatable("entity.block_additions.gamatoto")
                .withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA));
        this.setCustomNameVisible(true);
    }

    /**
     * Regla personalizada de spawn para controlar la rareza extrema de la entidad.
     */
    public static boolean checkGamatotoSpawnRules(
            EntityType<Gamatoto> entityType,
            LevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random) {

        // Si el intento de spawn es natural o por generación de mapa:
        if (spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION) {
            // Tirada de dados: Solo 1 de cada 20 intentos (5%) tiene éxito
            if (random.nextInt(20) != 0) {
                return false;
            }
        }

        // Mantiene la validación estándar de Mobs (bloques sólidos, luz, etc.)
        return checkMobSpawnRules(entityType, level, spawnType, pos, random);
    }

    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();
        offers.clear();

        ItemStack customBook1 = new ItemStack(Items.ENCHANTED_BOOK);
        customBook1.enchant(ModEnchantments.ARREBATO.get(), 1);
        ItemStack customBook2 = new ItemStack(Items.ENCHANTED_BOOK);
        customBook2.enchant(ModEnchantments.ARREBATO.get(), 2);
        ItemStack customBook3 = new ItemStack(Items.ENCHANTED_BOOK);
        customBook3.enchant(ModEnchantments.ARREBATO.get(), 3);

        offers.add(addOffer(new ItemStack(Items.NETHER_STAR, 1), new ItemStack(Items.BOOK, 1), customBook1, 1, 12, 0.05F));
        offers.add(addOffer(new ItemStack(Items.NETHER_STAR, 2), new ItemStack(Items.BOOK, 1), customBook2, 1, 16, 0.10F));
        offers.add(addOffer(new ItemStack(Items.NETHER_STAR, 3), new ItemStack(Items.BOOK, 1), customBook3, 1, 25, 0.25F));
    }

    private MerchantOffer addOffer(ItemStack item1, ItemStack item2, ItemStack item3, int max, int exp, float multiplier) {
        return new MerchantOffer(
                item1,
                item2,
                item3,
                max,
                exp,
                multiplier
        );
    }

    private boolean needsToSpawnCompanions = false;

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);

        if (!level.isClientSide()) {
            this.needsToSpawnCompanions = true;
        }

        return result;
    }

    @Override
    public void tick() {
        super.tick();

        // Posponemos la generación de los acompañantes al primer tick para no colisionar con la generación de chunks
        if (!this.level().isClientSide() && this.needsToSpawnCompanions) {
            this.needsToSpawnCompanions = false;
            if (this.level() instanceof ServerLevel serverLevel) {
                this.spawnCompanions(serverLevel);
            }
        }
    }

    private void spawnCompanions(ServerLevel level) {
        for (int i = 0; i < 2; i++) {
            Cat companion = EntityType.CAT.create(level);
            if (companion != null) {
                companion.moveTo(this.getX() + (i == 0 ? 1 : -1), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                companion.setOwnerUUID(this.getUUID());
                companion.setVariant(Objects.requireNonNull(BuiltInRegistries.CAT_VARIANT.get(CatVariant.WHITE)));

                level.addFreshEntity(companion);
                companion.setLeashedTo(this, true);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }
}