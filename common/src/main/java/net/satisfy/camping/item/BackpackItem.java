package net.satisfy.camping.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.satisfy.camping.block.BackpackBlock;
import net.satisfy.camping.inventory.BackpackContainer;
import net.satisfy.camping.block.BackpackType;
import net.satisfy.camping.block.entity.BackpackBlockEntity;
import net.satisfy.camping.client.screen.BackpackScreenHandler;
import org.jetbrains.annotations.NotNull;

public class BackpackItem extends BlockItem implements Equipable {

    private final ResourceLocation backpackTexture;
    public final BackpackType backpackType;

    public BackpackItem(Block block, ResourceLocation backpackTexture) {
        super(block, new Properties().fireResistant().stacksTo(1));

        this.backpackType = ((BackpackBlock) block).getBackpackType();
        this.backpackTexture = backpackTexture;
    }

    public ResourceLocation getBackpackTexture() {
        return backpackTexture;
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return ArmorMaterials.LEATHER.getEquipSound();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {

        ItemStack itemStack = player.getMainHandItem();

        if (level.isClientSide() || interactionHand == InteractionHand.OFF_HAND) {
            return InteractionResultHolder.fail(itemStack);
        }

        CompoundTag blockEntityTag = BlockItem.getBlockEntityData(itemStack);

        if (blockEntityTag != null) {

            if (blockEntityTag.contains("Items", 9)) {

                NonNullList<ItemStack> itemStacks = NonNullList.withSize(BackpackBlockEntity.CONTAINER_SIZE, ItemStack.EMPTY);

                ContainerHelper.loadAllItems(blockEntityTag, itemStacks);

                player.openMenu(new SimpleMenuProvider(new MenuConstructor() {
                    @Override
                    public @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                        return new BackpackScreenHandler(i, player.getInventory(), new BackpackContainer(itemStacks, player));
                    }
                }, Component.translatable("container.camping.backpack")));
            }
        }
        else {
            // todo: move most of this logic to networking as intended

            CompoundTag compoundTag = new CompoundTag();

            ContainerHelper.saveAllItems(compoundTag, NonNullList.withSize(24, ItemStack.EMPTY));

            itemStack.addTagElement("BlockEntityTag", compoundTag);

            NonNullList<ItemStack> itemStacks = NonNullList.withSize(BackpackBlockEntity.CONTAINER_SIZE, ItemStack.EMPTY);

            blockEntityTag = BlockItem.getBlockEntityData(itemStack);

            ContainerHelper.loadAllItems(blockEntityTag, itemStacks);

            player.openMenu(new SimpleMenuProvider(new MenuConstructor() {
                @Override
                public @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    return new BackpackScreenHandler(i, player.getInventory(), new BackpackContainer(itemStacks, player));
                }
            }, Component.translatable("container.camping.backpack")));

        }

        return InteractionResultHolder.sidedSuccess(player.getMainHandItem(), level.isClientSide());
    }
}
