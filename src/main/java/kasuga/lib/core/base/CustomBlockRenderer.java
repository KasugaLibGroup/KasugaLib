package kasuga.lib.core.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public abstract class CustomBlockRenderer<T extends Block> {
    private final Supplier<T> block;
    private boolean busy = false;

    public CustomBlockRenderer(Supplier<T> blockSupplier) {
        this.block = blockSupplier;
    }
    public synchronized void render(BlockState state, BlockPos pos, BlockAndTintGetter level
            , PoseStack stack, VertexConsumer consumer, RenderType type, int light) {}

    public T getBlock() {
        return block.get();
    }

    public boolean shouldRender(BlockState state, BlockPos pos, BlockAndTintGetter level) {
        return true;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public boolean isBusy() {
        return busy;
    }

    public boolean skipOriginalModelRenderering(BlockState state, BlockPos pos, BlockAndTintGetter level, RenderType type) {
        return true;
    }
}
