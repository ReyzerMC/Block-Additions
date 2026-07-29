package org.reyzer.blockAdditions.events.ObtainEvents;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.reyzer.blockAdditions.init.ModEnchantments;

public class HellForgedWitherEvent {
    @SubscribeEvent
    public void onWitherDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof WitherBoss wither) {
            int chosenLevel = getHellForgedLevel(wither.getRandom());

            if (chosenLevel != -1) {
                ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantedBookItem.addEnchantment(enchantedBook, new EnchantmentInstance(ModEnchantments.HELL_FORGED.get(), chosenLevel));

                ItemEntity bookEntity = new ItemEntity(
                        wither.level(),
                        wither.getX(),
                        wither.getY(),
                        wither.getZ(),
                        enchantedBook
                );

                event.getDrops().add(bookEntity);
            }
        }
    }

    private int getHellForgedLevel(RandomSource random) {
        double roll = random.nextDouble() * 100; // Número entre 0.0 y 99.99...

        if (roll < 3.0) {
            return 4;
        } else if (roll < 9.0) {
            return 3;
        } else if (roll < 19.0) {
            return 2;
        } else if (roll < 34.0) {
            return 1;
        }

        return -1;
    }
}
