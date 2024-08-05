package kasuga.lib.example_env;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.trains.track.TrackBlock;
import kasuga.lib.core.base.CustomTrackRenderer;
import kasuga.lib.core.create.TrackStateGenerator;
import kasuga.lib.example_env.block.track.SimpleTrackBlock;
import kasuga.lib.registrations.create.TrackReg;
import kasuga.lib.registrations.registry.CreateRegistry;
import net.minecraft.world.level.material.Material;

public class ExampleTracks {
    public static final CreateRegistry testRegistry = ExampleMain.testRegistry;
    public static final TrackStateGenerator.Builder stateBuilder =
            TrackStateGenerator.Builder.of("block/track/standard/")
                    .xRotation((state) -> 0)
                    .yRotation((state -> state.getValue(TrackBlock.SHAPE).getModelRotation()))
                    .addModelContext(
                            TrackStateGenerator.ModelBuilderContext.of
                                    (TrackStateGenerator.ModelActionType.PARENT,
                                            "track/standard",
                                            testRegistry.asResource("track/standard")))
                    .addModelContext(TrackStateGenerator.ModelBuilderContext.of
                            (TrackStateGenerator.ModelActionType.TEXTURE, "particle", null));

    public static final TrackReg<SimpleTrackBlock> exampleTrack =
            new TrackReg<SimpleTrackBlock>("standard_track")
                    .trackState(stateBuilder)
                    .trackNameSuffix("Train Track")
                    .material(Material.STONE)
                    .trackMaterial(ExampleTrackMaterial.exampleMaterial::getMaterial)
                    .pickaxeOnly()
                    .withBlockRenderer(block -> () -> new CustomTrackRenderer<SimpleTrackBlock>(block))
                    .addTags(AllTags.AllBlockTags.TRACKS.tag)
                    .addTags(AllTags.AllBlockTags.GIRDABLE_TRACKS.tag)
                    .addTags(AllTags.AllBlockTags.RELOCATION_NOT_SUPPORTED.tag)
                    .submit(testRegistry);

    public static final TrackReg<SimpleTrackBlock> tielessTrack =
            new TrackReg<SimpleTrackBlock>("tieless_track")
                    .trackState(stateBuilder)
                    .trackNameSuffix("Tieless Track")
                    .material(Material.STONE)
                    .trackMaterial(ExampleTrackMaterial.tielessMaterial::getMaterial)
                    .pickaxeOnly()
                    .addTags(AllTags.AllBlockTags.TRACKS.tag)
                    .addTags(AllTags.AllBlockTags.GIRDABLE_TRACKS.tag)
                    .addTags(AllTags.AllBlockTags.RELOCATION_NOT_SUPPORTED.tag)
                    .submit(testRegistry);

    public static void invoke(){}
}
