package org.reyzer.blockAdditions.events.ObtainEvents;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DragonEggCraftingHandler {
    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        // Recorremos el inventario de la mesa de crafteo donde se colocaron los materiales
        for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
            ItemStack stack = event.getInventory().getItem(i);

            // Si detectamos un Huevo de Dragón en la cuadrícula de crafteo
            if (stack.is(Items.DRAGON_EGG)) {
                // Le devolvemos 1 unidad para compensar la que la mesa de crafteo va a consumir
                stack.grow(1);
            }
        }
    }
}
