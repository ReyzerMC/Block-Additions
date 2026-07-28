package org.reyzer.blockAdditions.events;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.reyzer.blockAdditions.init.ModEnchantments;

public class HellForgedEvent {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        boolean onFire = player.isOnFire() || player.isInLava();

        if (onFire) {
            for (ItemStack stack : player.getArmorSlots()) {
                if (stack.isEmpty()) continue;

                int level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.HELL_FORGED.get(), stack);
                if (level <= 0) return;

                boolean shouldRepair = false;

                if (level == 1) {
                    shouldRepair = (player.tickCount % 40 == 0);
                } else {
                    shouldRepair = (player.tickCount % 20 == 0);
                }

                if (shouldRepair && stack.isDamaged()) {
                    int repairAmount = switch (level) {
                        case 1 -> 1;
                        case 2 -> 2;
                        case 3 -> 3;
                        case 4 -> 4;
                        default -> 0;
                    };

                    stack.setDamageValue(Math.max(0, stack.getDamageValue() - repairAmount));
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDamage(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        DamageSource source = event.getSource();

        if (source.is(DamageTypeTags.IS_FIRE)) {

            int piecesWithMaxLevel = 0;

            for (ItemStack stack : player.getArmorSlots()) {
                if (!stack.isEmpty()) {
                    int level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.HELL_FORGED.get(), stack);
                    if (level == 4) {
                        piecesWithMaxLevel++;
                    }
                }
            }
            if (piecesWithMaxLevel == 4) {
                event.setCanceled(true);
            }
        }
    }
}
