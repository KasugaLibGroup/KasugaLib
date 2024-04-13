package kasuga.lib.example_env.block.bogey;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.schematics.requirement.ISpecialBlockItemRequirement;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class SimpleBogeyBlock<T extends AbstractBogeyBlockEntity> extends AbstractBogeyBlock<T>
    implements IBE<T>, ProperWaterloggedBlock, ISpecialBlockItemRequirement {
    private final Set<TrackMaterial.TrackType> validTracks;
    private final double wheelPointSpacing, wheelRadius;
    private final TrackMaterial.TrackType defaultTrack;
    private final BogeyStyle bogeyStyle;
    private final Vec3 connctorAnchorOffset;
    private final NonNullSupplier<BlockEntityType<? extends AbstractBogeyBlockEntity>> entitySupplier;
    private final Class<? extends AbstractBogeyBlockEntity> entityClass;
    private final boolean allowSingleBogeyCarriage;

    @ParametersAreNonnullByDefault
    public SimpleBogeyBlock(
            Set<TrackMaterial.TrackType> validTracks, TrackMaterial.TrackType defaultTrack, Properties pProperties,
            BogeyStyle defaultStyle, BogeySizes.BogeySize size, double wheelPointSpacing,
            double wheelRadius, Vec3 connctorAnchorOffset, boolean allowSingleBogeyCarriage,
            NonNullSupplier<BlockEntityType<? extends AbstractBogeyBlockEntity>> entitySupplier, Class<? extends AbstractBogeyBlockEntity> entityClass) {
        super(pProperties, size);
        this.validTracks = validTracks;
        this.wheelRadius = wheelRadius;
        this.wheelPointSpacing = wheelPointSpacing;
        this.bogeyStyle = defaultStyle;
        this.defaultTrack = defaultTrack;
        this.allowSingleBogeyCarriage = allowSingleBogeyCarriage;
        this.connctorAnchorOffset = connctorAnchorOffset;
        this.entitySupplier = entitySupplier;
        this.entityClass = entityClass;
    }

    @Override
    public TrackMaterial.TrackType getTrackType(BogeyStyle style) {
        return defaultTrack;
    }

    @Override
    public double getWheelPointSpacing() {
        return wheelPointSpacing;
    }

    @Override
    public double getWheelRadius() {
        return wheelRadius;
    }

    @Override
    protected Vec3 getConnectorAnchorOffset() {
        return connctorAnchorOffset;
    }

    @Override
    public BogeyStyle getDefaultStyle() {
        return bogeyStyle;
    }

    @Override
    public Class<T> getBlockEntityClass() {
        return (Class<T>) entityClass;
    }

    @Override
    public BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) entitySupplier.get();
    }

    @Override
    public boolean allowsSingleBogeyCarriage() {
        return allowSingleBogeyCarriage;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return AllBlocks.RAILWAY_CASING.asStack();
    }

    @Override
    public BlockState getRotatedBlockState(BlockState state, Direction targetedFace) {
        return state;
    }

    private final List<Property<?>> properties_to_copy =
            ImmutableList.<Property<?>>builder().addAll(super.propertiesToCopy()).build();

    @Override
    public List<Property<?>> propertiesToCopy() {
        return properties_to_copy;
    }

    @Override
    public Set<TrackMaterial.TrackType> getValidPathfindingTypes(BogeyStyle style) {
        return validTracks;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public static class Builder {
        private final Supplier<BogeyStyle> style;
        private double wheelPointSpacing = 2.5d, wheelRadius = 0.915d;
        private Vec3 connectorAnchorOffset = new Vec3(0, 7 / 32f, 1);
        private boolean allowSingleBogeyCarriage = true;
        private TrackMaterial.TrackType[] validTypes = new TrackMaterial.TrackType[0];
        private Class<? extends AbstractBogeyBlockEntity> clazz = AbstractBogeyBlockEntity.class;
        private NonNullSupplier<BlockEntityType<? extends AbstractBogeyBlockEntity>> supplier = AllBlockEntityTypes.BOGEY::get;

        public Builder(Supplier<BogeyStyle> style) {
            this.style = style;
        }

        public Builder bogeyParams(double wheelRadius, double wheelPointSpacing) {
            this.wheelRadius = wheelRadius;
            this.wheelPointSpacing = wheelPointSpacing;
            return this;
        }

        public Builder anchorOffset(double x, double y, double z) {
            this.connectorAnchorOffset = new Vec3(x, y, z);
            return this;
        }

        public Builder allowSingleBogeyCarriage(boolean allow) {
            this.allowSingleBogeyCarriage = allow;
            return this;
        }

        public Builder validTrackTypes(TrackMaterial.TrackType... types) {
            this.validTypes = types;
            return this;
        }

        public <T extends AbstractBogeyBlockEntity> Builder bogeyBlockEntity(Class<T> clazz, NonNullSupplier<BlockEntityType<? extends AbstractBogeyBlockEntity>> supplier) {
            this.clazz = clazz;
            this.supplier = supplier;
            return this;
        }

        public <T extends AbstractBogeyBlockEntity> SimpleBogeyBlock<T> build(Properties properties, BogeySizes.BogeySize size) {
            return new SimpleBogeyBlock<>(Set.of(validTypes), validTypes.length == 0 ? TrackMaterial.TrackType.STANDARD : validTypes[0],
                properties, style.get(), size, wheelPointSpacing, wheelRadius,
                    connectorAnchorOffset, allowSingleBogeyCarriage, supplier, clazz);
        }
    }
}
