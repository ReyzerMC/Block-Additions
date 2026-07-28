package org.reyzer.blockAdditions.init;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.reyzer.blockAdditions.BlockAdditions;
import org.reyzer.blockAdditions.enchantments.*;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, BlockAdditions.MOD_ID);

    public static final RegistryObject<Enchantment> ARREBATO = ENCHANTMENTS.register("arrebato",
            () -> new Arrebato(
                    Enchantment.Rarity.UNCOMMON,
                    EnchantmentCategory.ARMOR,
                    EquipmentSlot.HEAD,
                    EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS,
                    EquipmentSlot.FEET
            ));

    public static final RegistryObject<Enchantment> BOND_OF_LIFE = ENCHANTMENTS.register("bond_of_life",
            () -> new BondOfLife(
                    Enchantment.Rarity.VERY_RARE,
                    EnchantmentCategory.WEAPON,
                    EquipmentSlot.MAINHAND
            ));

    public static final RegistryObject<Enchantment> THUNDERING = ENCHANTMENTS.register("thundering",
            () -> new Thundering(
                    Enchantment.Rarity.VERY_RARE,
                    EnchantmentCategory.WEAPON,
                    EquipmentSlot.MAINHAND
            ));

    public static final RegistryObject<Enchantment> WAVE = ENCHANTMENTS.register("wave",
            () -> new Wave(
                    Enchantment.Rarity.VERY_RARE,
                    EnchantmentCategory.WEAPON,
                    EquipmentSlot.MAINHAND
            ));
    public static final RegistryObject<Enchantment> HOT_STUFF = ENCHANTMENTS.register("hot_stuff",
            () -> new HotStuff(
                    Enchantment.Rarity.UNCOMMON,
                    EnchantmentCategory.DIGGER,
                    EquipmentSlot.MAINHAND
            ));
    public static final RegistryObject<Enchantment> TELEKINESIS = ENCHANTMENTS.register("telekinesis",
            () -> new Telekinesis(
                    Enchantment.Rarity.COMMON,
                    EnchantmentCategory.DIGGER,
                    EquipmentSlot.MAINHAND
            ));

    public static final RegistryObject<Enchantment> HELL_FORGED = ENCHANTMENTS.register("hell_forged",
            () -> new HellForged(
                    Enchantment.Rarity.VERY_RARE,
                    EnchantmentCategory.ARMOR,
                    EquipmentSlot.HEAD,
                    EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS,
                    EquipmentSlot.FEET
            ));

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
