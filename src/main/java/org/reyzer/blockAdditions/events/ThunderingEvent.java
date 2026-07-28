package org.reyzer.blockAdditions.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.reyzer.blockAdditions.init.ModEnchantments;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ThunderingEvent {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 3000; // 3 segundos en ms

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {

        // 1. Evitar bucles infinitos con la misma fuente de daño
        if (event.getSource().is(net.minecraft.world.damagesource.DamageTypes.LIGHTNING_BOLT)) {
            return;
        }

        LivingEntity victim = event.getEntity();

        if (!(victim.level() instanceof ServerLevel level)) {
            return;
        }

        Entity entity = event.getSource().getEntity();

        if (entity instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();

            if (!weapon.isEmpty()) {
                int enchantmentLevel = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.THUNDERING.get(), weapon);
                if (enchantmentLevel <= 0) return;

                UUID attackerUUID = attacker.getUUID();
                long currentTime = System.currentTimeMillis();

                // 2. CHECK DE COOLDOWN (Validar antes de crear entidades)
                if (cooldowns.containsKey(attackerUUID)) {
                    long lastUse = cooldowns.get(attackerUUID);
                    if (currentTime - lastUse < COOLDOWN_TIME) {
                        return; // Sigue en cooldown, no hace nada
                    }
                }

                // 3. CHECK DE PROBABILIDAD (35% de chance)
                if (ThreadLocalRandom.current().nextDouble() >= 0.35) {
                    return; // No superó la probabilidad
                }

                // 4. SI PASA AMBAS PRUEBAS: Registrar nuevo tiempo de cooldown
                cooldowns.put(attackerUUID, currentTime);

                // 5. Crear e invocar el rayo
                LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);

                if (lightningBolt != null) {
                    Vec3 victimPos = victim.position();
                    lightningBolt.moveTo(victimPos);

                    // Hacerlo visual o real según prefieras
                    level.addFreshEntity(lightningBolt);

                    // 6. Aplicar el daño personalizado
                    float lightningDmg = 5f * enchantmentLevel;

                    // Resetear invulnerability ticks para asegurar que el daño del rayo entre junto al golpe
                    victim.invulnerableTime = 0;
                    victim.hurt(level.damageSources().lightningBolt(), lightningDmg);
                }
            }
        }
    }
}
