package kasuga.lib.example_env.block.fluid;

import kasuga.lib.registrations.common.FluidReg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.extensions.IForgeFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class ExampleFluid extends ForgeFlowingFluid {


    public ExampleFluid(Properties properties) {
        super(properties);
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> pBuilder) {
        super.createFluidStateDefinition(pBuilder.add(LEVEL));
    }

    @Override
    public boolean isSource(FluidState fluidState) {
        return true;
    }

    @Override
    public int getAmount(@NotNull FluidState fluidState) {
        return isSource(fluidState) ? 8 : (Integer) fluidState.getValue(LEVEL);
    }

    public static class Flowing extends ExampleFluid {

        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return false;
        }
    }
}
