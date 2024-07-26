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
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final BackpackType backpackType;

    public BackpackBlock(Properties properties, BackpackType backpackType) {
        super(properties);
        this.backpackType = backpackType;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    public BackpackType getBackpackType() {
        return this.backpackType;
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

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        FluidState fluidState = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BackpackBlockEntity(blockPos, blockState);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    /* VoxelShape */

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

    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }
}
