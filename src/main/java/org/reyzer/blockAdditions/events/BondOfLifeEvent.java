package org.reyzer.blockAdditions.events;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.reyzer.blockAdditions.init.ModEffects;
import org.reyzer.blockAdditions.init.ModEnchantments;

public class BondOfLifeEvent {
    @SubscribeEvent
    public void onLivingHurt(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.hasEffect(ModEffects.BOND_OF_LIFE.get())) {
            MobEffectInstance effectInstance = entity.getEffect(ModEffects.BOND_OF_LIFE.get());

            if (effectInstance != null) {
                int amplifier = effectInstance.getAmplifier();

                float multiplier = 1.0f + (0.20f * (amplifier + 1));

                float originalDamage = event.getAmount();
                event.setAmount(originalDamage * multiplier);
            }
        }
    }

    @SubscribeEvent
    public void onEntityHit(LivingAttackEvent event) {
        Entity entity = event.getSource().getEntity();
        LivingEntity victim = event.getEntity();

        if (entity instanceof LivingEntity livingAttacker) {
            ItemStack weapon = livingAttacker.getMainHandItem();

            if (!weapon.isEmpty()) {
                int level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.BOND_OF_LIFE.get(), weapon);
                if (level <= 0) return;

                int duration = (4 + (level * 2)) *20;
                int ampliffier = level - 1;

                victim.addEffect(new MobEffectInstance(ModEffects.BOND_OF_LIFE.get(), duration, ampliffier, false, true));
            }
         }
    }
}
