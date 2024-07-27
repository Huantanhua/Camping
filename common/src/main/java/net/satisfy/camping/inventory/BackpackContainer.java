package net.satisfy.camping.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.satisfy.camping.block.entity.BackpackBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class BackpackContainer implements Container, StackedContentsCompatible {

//    private ItemStack packStack;
    private NonNullList<ItemStack> stacks = NonNullList.withSize(BackpackBlockEntity.CONTAINER_SIZE, ItemStack.EMPTY);

    @Nullable
    private List<ContainerListener> listeners;

    private @Nullable Player player;

    public BackpackContainer(NonNullList<ItemStack> itemStacks) {
        this.stacks = itemStacks;
    }

    public BackpackContainer(NonNullList<ItemStack> itemStacks, Player player) {
        this.stacks = itemStacks;
        this.player = player;
    }

//    public BackpackContainer(ItemStack packStack) {
//        this.packStack = packStack;
//        AtomicInteger index = new AtomicInteger();
//        BackpackItem.getContents(packStack).forEach(
//                stack -> stacks.set(index.getAndIncrement(), stack)
//        );
//    }

    @Override
    public int getContainerSize() {
        return BackpackBlockEntity.CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() { return false; }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return stacks.get(i);
    }

    @Override
    public @NotNull ItemStack removeItem(int i, int j) {
        return ContainerHelper.removeItem(stacks, i, j);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(this.stacks, i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        stacks.set(i, itemStack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return !player.isDeadOrDying();
    }

    @Override
    public void clearContent() {
        this.stacks.clear();
        this.setChanged();
    }

    @Override
    public void fillStackedContents(StackedContents stackedContents) {
        for (ItemStack itemStack : this.stacks) {
            stackedContents.accountStack(itemStack);
        }
        this.setChanged();
    }

    private void setStackNbt(int i, ItemStack stack) {

//        if (this.player == null) {
//            return;
//        }
//
//        ItemStack itemStack = this.player.getMainHandItem();
//
//        CompoundTag compoundTag = BlockItem.getBlockEntityData(itemStack);
//
//        if (compoundTag != null) {
//            if (compoundTag.contains("Items", 9)) {
//                NonNullList<ItemStack> nonNullList = NonNullList.withSize(24, ItemStack.EMPTY);
//                ContainerHelper.loadAllItems(compoundTag, nonNullList);
//            }
//        }

        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag compoundTag2 = tag.getCompound("BlockEntityTag");
        ListTag listTag = compoundTag2.getList("Items", 9);
        if (listTag.isEmpty()) {
            listTag = new ListTag();
            listTag.addAll(
                    NonNullList.withSize(BackpackBlockEntity.CONTAINER_SIZE, new CompoundTag())
            );
        }


        listTag.set(i, stack.save(new CompoundTag()));
        compoundTag2.put("Items", listTag);
        tag.put("BlockEntityTag", compoundTag2);
        stack.setTag(tag);

    }

//    @Override
//    public void setChanged() {
//
//
//
//        AtomicInteger counter = new AtomicInteger();
//        this.stacks.forEach(stack -> setStackNbt(counter.getAndIncrement(), stack));
//
////        if (this.player == null || player.level().isClientSide()) {
////            return;
////        }
////
////        System.out.println("called setChanged");
//    }

    @Override
    public void setChanged() {
        if (this.listeners != null) {
            Iterator var1 = this.listeners.iterator();

            while(var1.hasNext()) {
                ContainerListener containerListener = (ContainerListener)var1.next();
                containerListener.containerChanged(this);
            }
        }

    }

    public List<ItemStack> getStacks() {
        return stacks;
    }
}
