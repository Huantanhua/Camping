package net.satisfy.camping.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.satisfy.camping.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class TentBlock extends HorizontalDirectionalBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF;

    static {
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    }

    public TentBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState());
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
        if (!(this instanceof TentMainBlock)) {
            return ObjectRegistry.TENT_MAIN.get("red").get().getCloneItemStack(getter, pos, state);
        }
        return super.getCloneItemStack(getter, pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @SuppressWarnings("deprecation")
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (entity instanceof Ravager && level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            level.destroyBlock(blockPos, true, entity);
        }
        super.entityInside(blockState, level, blockPos, entity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
}
