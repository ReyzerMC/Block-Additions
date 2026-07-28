package org.reyzer.blockAdditions.events;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.reyzer.blockAdditions.init.ModEnchantments;

public class ArrebatoEvent {

    @SubscribeEvent
    public void onLivingHurt(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Iterable<ItemStack> armorSlots = player.getArmorSlots();

        int lowestLevel = Integer.MAX_VALUE;
        int armorPieceCount = 0;

        for (ItemStack piece : armorSlots) {
            if (piece.isEmpty()) return;

            armorPieceCount++;

            int level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.ARREBATO.get(), piece);
            if (level <= 0) return;

            if (level < lowestLevel) {
                lowestLevel = level;
            }
        }

        if (armorPieceCount < 4 || lowestLevel == Integer.MAX_VALUE || lowestLevel <= 0) return;

        double maxHealth = player.getMaxHealth();
        double healthAfterDamage = player.getHealth() - event.getAmount();

        if ((healthAfterDamage / maxHealth) <= 0.15) {
            applyLowHealthEffects(player, lowestLevel);
        }
    }

    private void applyLowHealthEffects(Player player, int level) {
        switch (level) {
            case 1 -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 0, false, true)); // Resistance I
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 160, 0, false, true));      // Regen I
            }
            case 2 -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 1, false, true)); // Resistance II
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0, false, true));     // Strength I
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0, false, true));      // Regen I
            }
            case 3 -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 2, false, true)); // Resistance III
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1, false, true));      // Regen II
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 160, 1, false, true));     // Strength II
            }
            default -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 280, level - 2, false, true));
        }
    }
}
