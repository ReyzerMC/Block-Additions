package org.reyzer.blockAdditions.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.reyzer.blockAdditions.init.ModEnchantments;

import java.util.List;
import java.util.Optional;

public class TelekinesisEvent {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        LevelAccessor level = event.getLevel();
        if (level.isClientSide()) return;

        Player player = event.getPlayer();
        if (player == null || player.isCreative()) return;

        ItemStack tool = player.getMainHandItem();
        int enchantmentLevel = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.TELEKINESIS.get(), tool);
        if (enchantmentLevel <= 0) return;

        if (level instanceof ServerLevel serverLevel) {
            BlockPos pos = event.getPos();
            BlockState state = event.getState();
            BlockEntity blockEntity = serverLevel.getBlockEntity(pos);

            LootParams.Builder builder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                    .withParameter(LootContextParams.TOOL, tool)
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);

            List<ItemStack> drops = state.getDrops(builder);
            if (drops.isEmpty()) return;

            int hotStuffLevel = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.HOT_STUFF.get(), tool);
            if (hotStuffLevel > 0) {
                for (int i = 0; i < drops.size(); i++) {
                    ItemStack drop = drops.get(i);
                    Container container = new SimpleContainer(drop);
                    Optional<SmeltingRecipe> recipe = serverLevel.getRecipeManager()
                            .getRecipeFor(RecipeType.SMELTING, container, serverLevel);

                    if (recipe.isPresent()) {
                        ItemStack cookedResult = recipe.get().getResultItem(serverLevel.registryAccess()).copy();
                        cookedResult.setCount(drop.getCount());
                        drops.set(i, cookedResult);
                    }
                }
            }

            for (ItemStack drop : drops) {
                ItemStack stackToAdd = drop.copy();
                player.getInventory().add(stackToAdd);

                if (!stackToAdd.isEmpty()) {
                    player.drop(stackToAdd, false);
                }
            }

            serverLevel.destroyBlock(pos, false, player);
            event.setExpToDrop(0);
            event.setCanceled(true);
        }
    }
}