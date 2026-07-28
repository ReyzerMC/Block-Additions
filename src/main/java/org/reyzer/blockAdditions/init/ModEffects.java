package org.reyzer.blockAdditions.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.reyzer.blockAdditions.BlockAdditions;
import org.reyzer.blockAdditions.effects.BondOfLifeEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BlockAdditions.MOD_ID);

    public static final RegistryObject<MobEffect> BOND_OF_LIFE = MOB_EFFECTS.register("bond_of_life",
            () -> new BondOfLifeEffect(MobEffectCategory.HARMFUL, 0x990000));
}
