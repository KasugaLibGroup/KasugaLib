package kasuga.lib.example_env.fluid;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public class ExampleFluidBlock extends LiquidBlock {
    public ExampleFluidBlock(java.util.function.Supplier<? extends FlowingFluid> pFluid, BlockBehaviour.Properties pProperties) {
        super(pFluid, pProperties);
    }
}
