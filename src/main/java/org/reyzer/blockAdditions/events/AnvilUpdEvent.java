package org.reyzer.blockAdditions.events;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.reyzer.blockAdditions.init.ModEnchantments;

public class AnvilUpdEvent {
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        int levelLeft = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.ARREBATO.get(), left);
        int levelRight = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.ARREBATO.get(), right);

        if (levelLeft > 0 && levelRight > 0 && levelLeft >= levelRight) {
            event.setCanceled(true);
        }
    }
}
