package kasuga.lib.example_env.boundary;

import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import kasuga.lib.core.create.boundary.BoundarySegmentRegistry;
import kasuga.lib.example_env.ExampleMain;
import kasuga.lib.registrations.common.BlockEntityReg;
import kasuga.lib.registrations.common.BlockReg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ExampleBoundaryModule {
    public static EdgePointType<ExampleBoundary> EXAMPLE_BOUNDARY =
            EdgePointType.register(
                    new ResourceLocation("kasuga_lib","example_boundary"),
                    ExampleBoundary::new
            );


    public static BlockReg<ExampleBoundaryBlock> EXAMPLE_BOUNDARY_BLOCK;

    public static NonNullBiFunction<? super Block, Item.Properties, TrackTargetingBlockItem> EXAMPLE_ITEM_TYPE =
            TrackTargetingBlockItem.ofType(EXAMPLE_BOUNDARY);


    static {
        EXAMPLE_BOUNDARY_BLOCK =
                new BlockReg<ExampleBoundaryBlock>("example_boundary")
                        .blockType(ExampleBoundaryBlock::new)
                        .withItem((p)->EXAMPLE_ITEM_TYPE.apply(EXAMPLE_BOUNDARY_BLOCK.getBlock(), p), null)
                        .submit(ExampleMain.testRegistry);
    }

    public static BlockEntityReg<ExampleBoundaryBlockEntity> EXAMPLE_BOUNDARY_BLOCK_ENTITY =
            new BlockEntityReg<ExampleBoundaryBlockEntity>("example_boundary_block_entity")
                    .blockEntityType(ExampleBoundaryBlockEntity::new)
                    .addBlock(EXAMPLE_BOUNDARY_BLOCK)
                    .submit(ExampleMain.testRegistry);


    public static ResourceLocation EXAMPLE_SEGMENT_FEATURE = new ResourceLocation("kasuga_lib","example_boundary");
    public static void invoke() {
        BoundarySegmentRegistry.register(
                EXAMPLE_SEGMENT_FEATURE,
                EXAMPLE_BOUNDARY,
                ExampleSegment::new
        );
    }
}
