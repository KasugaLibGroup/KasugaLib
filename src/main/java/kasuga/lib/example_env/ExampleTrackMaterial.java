package kasuga.lib.example_env;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.AllPartialModels;
import kasuga.lib.example_env.block.track.SimpleTrackBlock;
import kasuga.lib.registrations.create.TrackMaterialReg;
import kasuga.lib.registrations.registry.CreateRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class ExampleTrackMaterial {

    public static final CreateRegistry testRegistry = ExampleMain.testRegistry;

    public static final SimpleTrackBlock.Builder builder =
            new SimpleTrackBlock.Builder(() -> AllExampleBogey.standardBogey.getEntry().get());

    public static final TrackMaterialReg exampleMaterial = new TrackMaterialReg("standard")
            .lang("standard_track")
            .block(() -> ExampleTracks.exampleTrack)
            .trackParticle(new ResourceLocation("block/palettes/stone_types/polished/andesite_cut_polished"))
            .type(testRegistry.asResource("standard"), builder::build)
            .customModel(
                    () -> new PartialModel(testRegistry.asResource("block/track/standard/tie")),
                    () -> AllPartialModels.TRACK_SEGMENT_LEFT,
                    () -> AllPartialModels.TRACK_SEGMENT_RIGHT
            )
            .simpleTrackModelOffset(0.755f)
            .sleeper(Blocks.ANDESITE_SLAB)
            .submit(testRegistry);

    public static final TrackMaterialReg tielessMaterial = new TrackMaterialReg("tieless")
            .lang("tieless_track")
            .block(() -> ExampleTracks.tielessTrack)
            .trackParticle(new ResourceLocation("block/palettes/stone_types/polished/andesite_cut_polished"))
            .type(testRegistry.asResource("tieless"), builder::build)
            .customModel(
                    () -> new PartialModel(testRegistry.asResource("empty_model")),
                    () -> new PartialModel(testRegistry.asResource("block/track/standard/segment_left")),
                    () -> new PartialModel(testRegistry.asResource("block/track/standard/segment_right"))
            )
            .simpleTrackModelOffset(0.755f)
            .sleeper(Blocks.ANDESITE_SLAB)
            .submit(testRegistry);
    public static void invoke(){}
}
