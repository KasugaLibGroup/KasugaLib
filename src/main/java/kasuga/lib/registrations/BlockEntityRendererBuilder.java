package kasuga.lib.registrations;

import kasuga.lib.registrations.common.BlockEntityReg;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


/**
 * A function interface that provides a block entity renderer. If you want to use a block entity renderer on your block and
 * block entity, you should first override the {@link Block#getRenderShape(BlockState)} method under your block class.
 * Then, pass a block entity renderer builder into {@link BlockEntityReg#withRenderer(BlockEntityReg.BlockEntityRendererBuilder)} method.
 * The lib would deal with it for you.
 * @param <T> the block entity your renderer belongs to.
 */
public interface BlockEntityRendererBuilder<T extends BlockEntity> {
    BlockEntityRenderer<T> build(BlockEntityRendererProvider.Context context);
}