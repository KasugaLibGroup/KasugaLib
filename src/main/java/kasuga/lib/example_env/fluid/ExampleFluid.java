package kasuga.lib.example_env.fluid;

import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class ExampleFluid extends ForgeFlowingFluid {

    public ExampleFluid(ForgeFlowingFluid.Properties properties) {
        super(properties);
        registerDefaultState(this.getStateDefinition().any().setValue(LEVEL, 1));
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> pBuilder) {
        super.createFluidStateDefinition(pBuilder);
        pBuilder.add(LEVEL);
    }

    @Override
    public Item getBucket() {
        return AllExampleElements.exampleFluid.bucket();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState pState, BlockGetter pLevel, BlockPos pPos, Fluid pFluid, Direction pDirection) {
        return false;
    }

    @Override
    public Vec3 getFlow(BlockGetter pBlockReader, BlockPos pPos, FluidState pFluidState) {
        return new Vec3(0, 0, 0);
    }

    @Override
    public int getTickDelay(LevelReader pLevel) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    public float getHeight(FluidState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.getValue(LEVEL);
    }

    @Override
    public float getOwnHeight(FluidState pState) {
        return 0;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState pState) {
        return AllExampleElements.exampleFluid.legacyBlock().defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState pState) {
        return true;
    }

    @Override
    public int getAmount(FluidState pState) {
        return 0;
    }

    @Override
    public VoxelShape getShape(FluidState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.box(pPos.getX(), pPos.getY(), pPos.getZ(), pPos.getX() + 1, pPos.getY() + 1, pPos.getZ() + 1);
    }
}
