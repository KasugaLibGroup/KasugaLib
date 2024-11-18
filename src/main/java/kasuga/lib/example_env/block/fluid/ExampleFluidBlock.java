package kasuga.lib.example_env.block.fluid;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.IFluidBlock;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ExampleFluidBlock extends LiquidBlock {

    public ExampleFluidBlock(Supplier<? extends ForgeFlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(LEVEL, 8);
    }
}
