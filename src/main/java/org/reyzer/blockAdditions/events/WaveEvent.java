package org.reyzer.blockAdditions.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.reyzer.blockAdditions.entities.WaveEntity;
import org.reyzer.blockAdditions.init.ModEnchantments;
import org.reyzer.blockAdditions.init.ModEntities;

public class WaveEvent {

    // 60 Ticks = 3 Segundos (20 Ticks = 1 Segundo)
    private static final int COOLDOWN_TICKS = 60;

    @SubscribeEvent
    public void onEntityAttack(AttackEntityEvent event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide() && player.getAttackStrengthScale(0.5F) >= 0.9F) {
            ItemStack mainHand = player.getMainHandItem();

            int waveLevel = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.WAVE.get(), mainHand);
            if (waveLevel <= 0) return;

            if (player.getCooldowns().isOnCooldown(mainHand.getItem())) {
                return;
            }

            WaveEntity wave = new WaveEntity(ModEntities.WAVE_PROJECTILE.get(), player.level());
            wave.setOwner(player);
            wave.setWaveLevel(waveLevel);

            Vec3 look = player.getLookAngle();
            wave.setPos(player.getX() + look.x, player.getEyeY() - 0.7, player.getZ() + look.z);
            wave.shoot(look.x, look.y, look.z, 1.2F, 0.0F);

            player.level().addFreshEntity(wave);

            player.getCooldowns().addCooldown(mainHand.getItem(), COOLDOWN_TICKS);
        }
    }
}