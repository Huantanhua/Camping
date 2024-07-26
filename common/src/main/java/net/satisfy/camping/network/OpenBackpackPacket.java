package net.satisfy.camping.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.satisfy.camping.Camping;

public class OpenBackpackPacket {
    public static final ResourceLocation ID = new ResourceLocation(Camping.MODID, "open_backpack");
    private final ItemStack backpackItem;

    public OpenBackpackPacket(ItemStack backpackItem) {
        this.backpackItem = backpackItem;
    }

    public OpenBackpackPacket(FriendlyByteBuf buf) {
        this.backpackItem = buf.readItem();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(backpackItem);
    }

//    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
//        NetworkManager.PacketContext context = contextSupplier.get();
//        ServerPlayer player = (ServerPlayer) context.getPlayer();
//        context.queue(() -> {
//            if (backpackItem.getItem() instanceof BackpackItem) {
//                BackpackContainer backpackContainer = BackpackItem.getContainer(backpackItem);
//
//                MenuProvider provider = new MenuProvider() {
//                    @Override
//                    public @NotNull Component getDisplayName() {
//                        return Component.translatable("container.camping.backpack");
//                    }
//
//                    @Override
//                    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
//                        return new BackpackScreenHandler(syncId, playerInventory, backpackContainer);
//                    }
//                };
//
//                player.openMenu(provider);
//            }
//        });
//    }

    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ID, (buf, contextSupplier) -> {
//            OpenBackpackPacket packet = new OpenBackpackPacket(buf);
//            packet.handle(() -> contextSupplier);
            // todo: fix packet handling
        });
    }
}
