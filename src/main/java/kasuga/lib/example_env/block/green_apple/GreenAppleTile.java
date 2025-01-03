package kasuga.lib.example_env.block.green_apple;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.gui.GuiInstance;
import kasuga.lib.core.client.render.RendererUtil;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class GreenAppleTile extends BlockEntity {

    public float sec = 0f;
    public boolean direction = false;
    public boolean saved = false;
    public GreenAppleTile(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public GreenAppleTile(BlockPos pos, BlockState state) {
        this(AllExampleElements.greenAppleTile.getType(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return AABB.ofSize(RendererUtil.blockPos2Vec3(getBlockPos()), 5, 5, 5);
    }

    @Override
    public void onChunkUnloaded() {}
}
