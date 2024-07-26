package net.satisfy.camping.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.camping.client.screen.BackpackScreenHandler;
import net.satisfy.camping.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BackpackBlockEntity extends BlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private Component customName;

    public static final int CONTAINER_SIZE = 24;

    public BackpackBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.BACKPACK_BLOCK_ENTITY.get(), pos, state);
    }

    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public Component getCustomName() {
        return customName;
    }

    public void setCustomName(Component customName) {
        this.customName = customName;
    }
}
