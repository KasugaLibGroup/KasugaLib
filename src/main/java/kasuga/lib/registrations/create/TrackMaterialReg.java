package kasuga.lib.registrations.create;

import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackMaterialFactory;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.create.TrackType;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class TrackMaterialReg extends Reg {
    private TrackType trackType = null;
    private ResourceLocation trackParticle = null;
    private Supplier<PartialModel> tieModel, leftSegModel, rightSegModel;
    private NonNullSupplier<NonNullSupplier<? extends TrackBlock>> blockSupplier = null;
    private ItemLike[] sleepers = null, rails = null;
    private String langKey = "";
    private boolean replaceable = false;
    private TrackOffsetIdentifier
            rightOffset = (segment -> segment.scale(.965f));
    private TrackMaterial material = null;
    private boolean isCustomRendered = false;
    public TrackMaterialReg(String registrationKey) {
        super(registrationKey);
    }

    public TrackMaterialReg type(TrackType trackType) {
        this.trackType = trackType;
        return this;
    }

    public TrackMaterialReg type(ResourceLocation trackId, TrackMaterial.TrackType.TrackBlockFactory factory) {
        this.trackType = TrackType.of(trackId, factory);
        return this;
    }

    public TrackMaterialReg trackParticle(ResourceLocation particle) {
        this.trackParticle = particle;
        return this;
    }

    public TrackMaterialReg block(NonNullSupplier<NonNullSupplier<? extends TrackBlock>> block) {
        this.blockSupplier = block;
        return this;
    }

    public TrackMaterialReg block(Supplier<TrackReg<?>> reg) {
        this.blockSupplier = () -> reg.get().getEntry();
        return this;
    }

    public TrackMaterialReg sleeper(ItemLike... items) {
        this.sleepers = items;
        return this;
    }

    public TrackMaterialReg rails(ItemLike... rali) {
        this.rails = rali;
        return this;
    }

    public TrackMaterialReg isReplaceable(boolean replaceable) {
        this.replaceable = replaceable;
        return this;
    }

    public TrackMaterialReg customModel(Supplier<PartialModel> tie, Supplier<PartialModel> leftSegment, Supplier<PartialModel> rightSegment) {
        this.tieModel = tie;
        this.leftSegModel = leftSegment;
        this.rightSegModel = rightSegment;
        isCustomRendered = true;
        return this;
    }

    public TrackMaterialReg defaultModel() {
        isCustomRendered = false;
        return this;
    }

    public TrackMaterialReg simpleTrackModelOffset(float halfDistance) {
        rightOffset = (segment -> segment.scale(halfDistance));
        return this;
    }

    public TrackMaterialReg defaultTrackModelOffset() {
        rightOffset = (segment -> segment.scale(.965f));
        return this;
    }

    public TrackMaterialReg customTrackModelOffset(TrackOffsetIdentifier left, TrackOffsetIdentifier right) {
        rightOffset = right;
        return this;
    }

    public TrackMaterialReg lang(String langKey) {
        this.langKey = langKey;
        return this;
    }
    @Override
    public TrackMaterialReg submit(SimpleRegistry registry) {
        TrackMaterialFactory factory = TrackMaterialFactory.make(registry.asResource(registrationKey))
                .lang(langKey)
                .block(blockSupplier)
                .trackType(trackType);
        if (trackParticle != null) factory.particle(trackParticle);
        if (sleepers != null) factory.sleeper(sleepers);
        if (rails != null) factory.rails(rails);
        if (isCustomRendered)
            factory.customModels(() -> tieModel, () -> leftSegModel, () -> rightSegModel);
        else
            factory.defaultModels();

        material = factory.build();
        KasugaLib.STACKS.cacheTrackMaterialIn(this);
        return this;
    }

    public TrackType getTrackType() {
        return trackType;
    }

    public TrackMaterial getMaterial() {
        return material;
    }

    public ResourceLocation getTrackParticle() {
        return trackParticle;
    }

    public TrackOffsetIdentifier trackOffsets() {
        return rightOffset;
    }

    @Override
    public String getIdentifier() {
        return "track_material";
    }

    @FunctionalInterface
    public interface TrackOffsetIdentifier {
        Vec3 apply(Vec3 vector);
    }
}
