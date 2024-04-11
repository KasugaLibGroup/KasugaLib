package kasuga.lib.example_env;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.Create;
import kasuga.lib.example_env.block.track.StandardTrackBlock;
import kasuga.lib.registrations.create.TrackMaterialReg;
import kasuga.lib.registrations.registry.CreateRegistry;
import net.minecraft.world.level.block.Blocks;

public class ExampleTrackMaterial {

    public static final CreateRegistry testRegistry = AllExampleElements.testRegistry;

    public static final TrackMaterialReg exampleMaterial = new TrackMaterialReg("standard")
            .lang("standard_track")
            .block(() -> ExampleTracks.exampleTrack)
            .trackParticle(Create.asResource("block/palettes/stone_types/polished/andesite_cut_polished"))
            .type(testRegistry.asResource("standard"), StandardTrackBlock::new)
            .customModel(
                    () -> new PartialModel(testRegistry.asResource("block/track/standard/tie")),
                    () -> new PartialModel(testRegistry.asResource("block/track/standard/segment_left")),
                    () -> new PartialModel(testRegistry.asResource("block/track/standard/segment_right"))
            )
            .simpleTrackModelOffset(0.755f)
            .sleeper(Blocks.ANDESITE_SLAB)
            .submit(testRegistry);
    public static void invoke(){}
}
