package net.satisfy.camping.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.camping.util.CampingUtil;
import net.satisfy.camping.block.entity.BackpackBlockEntity;
import net.satisfy.camping.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class BackpackBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final ResourceLocation CONTENTS = new ResourceLocation("contents");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final BackpackType backpackType;

    public BackpackBlock(Properties properties, BackpackType backpackType) {
        super(properties);
        this.backpackType = backpackType;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    public static final Map<BackpackType, Map<Direction, VoxelShape>> SHAPES = net.minecraft.Util.make(new HashMap<>(), map -> {
        map.put(BackpackType.SMALL_BACKPACK, generateShapes(BackpackBlockShapes.SMALL_BACKPACK));
        map.put(BackpackType.LARGE_BACKPACK, generateShapes(BackpackBlockShapes.LARGE_BACKPACK));
        map.put(BackpackType.WANDERER_BACKPACK, generateShapes(BackpackBlockShapes.WANDERER_BACKPACK));
        map.put(BackpackType.WANDERER_BAG, generateShapes(BackpackBlockShapes.WANDERER_BAG));
        map.put(BackpackType.GOODYBAG, generateShapes(BackpackBlockShapes.GOODYBAG));
        map.put(BackpackType.SHEEPBAG, generateShapes(BackpackBlockShapes.SHEEPBAG));
    });

    private static Map<Direction, VoxelShape> generateShapes(Supplier<VoxelShape> shapeSupplier) {
        Map<Direction, VoxelShape> shapeMap = new HashMap<>();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            shapeMap.put(direction, CampingUtil.rotateShape(Direction.NORTH, direction, shapeSupplier.get()));
        }
        return shapeMap;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.get(backpackType).get(state.getValue(FACING));
    }

    public BackpackType getBackpackType() {
        return this.backpackType;
    }

    public enum BackpackType {
        WANDERER_BACKPACK, LARGE_BACKPACK, SMALL_BACKPACK, WANDERER_BAG, SHEEPBAG, GOODYBAG;
    }

//    @Override
//    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
//        if (!level.isClientSide) {
//            BlockEntity blockEntity = level.getBlockEntity(blockPos);
//            if (blockEntity instanceof BackpackBlockEntity) {
//                ItemStack itemStack = new ItemStack(blockState.getBlock());
//                CompoundTag tag = new CompoundTag();
//                ((BackpackBlockEntity) blockEntity).saveAdditional(tag);
//                itemStack.addTagElement("BlockEntityTag", tag);
//                double x = blockPos.getX() + 0.5;
//                double y = blockPos.getY() + 0.5;
//                double z = blockPos.getZ() + 0.5;
//                ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
//                itemEntity.setDefaultPickUpDelay();
//                level.addFreshEntity(itemEntity);
//            }
//        }
//        super.playerWillDestroy(level, blockPos, blockState, player);
//    }

//    @Override
//    public @NotNull List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
//        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
//        if (blockEntity instanceof BackpackBlockEntity) {
//            builder = builder.withDynamicDrop(CONTENTS, (consumer) -> {
//                for (int i = 0; i < ((BackpackBlockEntity) blockEntity).getContainerSize(); ++i) {
//                    consumer.accept(((BackpackBlockEntity) blockEntity).getItems().get(i));
//                }
//            });
//        }
//        return super.getDrops(blockState, builder);
//    }

//    @Override
//    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
//        if (itemStack.hasCustomHoverName()) {
//            BlockEntity blockEntity = level.getBlockEntity(blockPos);
//            if (blockEntity instanceof BackpackBlockEntity) {
//                ((BackpackBlockEntity) blockEntity).setCustomName(itemStack.getHoverName());
//            }
//        }
//    }

//    @Override
//    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean isMoving) {
//        if (!blockState.is(newState.getBlock())) {
//            BlockEntity blockEntity = level.getBlockEntity(blockPos);
//            if (blockEntity instanceof BackpackBlockEntity) {
//                level.updateNeighbourForOutputSignal(blockPos, blockState.getBlock());
//            }
//            super.onRemove(blockState, level, blockPos, newState, isMoving);
//        }
//    }

//    @Override
//    public @NotNull ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
//        ItemStack itemStack = super.getCloneItemStack(blockGetter, blockPos, blockState);
//        blockGetter.getBlockEntity(blockPos, EntityTypeRegistry.BACKPACK_BLOCK_ENTITY.get()).ifPresent(blockEntity -> {
//            CompoundTag tag = new CompoundTag();
//            blockEntity.saveAdditional(tag);
//            itemStack.addTagElement("BlockEntityTag", tag);
//        });
//        return itemStack;
//    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos));
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        FluidState fluidState = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BackpackBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }
}
