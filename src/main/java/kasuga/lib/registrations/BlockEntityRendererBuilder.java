package kasuga.lib.registrations;

import kasuga.lib.registrations.common.BlockEntityReg;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


/**
 * 这是一个提供方块实体渲染器的函数接口。如果你想在你的方块和方块实体上使用方块实体渲染器，你应该首先在你的方块类下重写{@link Block#getRenderShape(BlockState)}方法。
 * 然后，将一个方块实体渲染器构建器传递给{@link BlockEntityReg#withRenderer(BlockEntityReg.BlockEntityRendererBuilder)}方法。
 * KasugaLib会为你处理它。
 * @param <T> 你的渲染器所属的方块实体。
 * A function interface that provides a block entity renderer. If you want to use a block entity renderer on your block and
 * block entity, you should first override the {@link Block#getRenderShape(BlockState)} method under your block class.
 * Then, pass a block entity renderer builder into {@link BlockEntityReg#withRenderer(BlockEntityReg.BlockEntityRendererBuilder)} method.
 * The lib would deal with it for you.
 * @param <T> the block entity your renderer belongs to.
 */
public interface BlockEntityRendererBuilder<T extends BlockEntity> {
    BlockEntityRenderer<T> build(BlockEntityRendererProvider.Context context);
}