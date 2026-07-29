package org.reyzer.blockAdditions.entities;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.reyzer.blockAdditions.init.ModEnchantments;

import javax.annotation.Nullable;

public class Gamatoto extends WanderingTrader {
    public Gamatoto(EntityType<? extends WanderingTrader> type, Level level) {
        super(type, level);
        this.setCustomName(Component.literal("Gamatoto").withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA));
        this.setCustomNameVisible(true);
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

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);

        if (!level.isClientSide()) {
            this.spawnCompanions(level);
        }

        return result;
    }

    private void spawnCompanions(ServerLevelAccessor level) {
        for (int i = 0; i < 2; i++) {
            Cat companion = EntityType.CAT.create(level.getLevel());
            if (companion != null) {
                companion.moveTo(this.getX() + (i == 0 ? 1 : -1), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                companion.setLeashedTo(this, true);
                companion.setOwnerUUID(this.getUUID());
                level.addFreshEntity(companion);
            }
        }
    }
}
