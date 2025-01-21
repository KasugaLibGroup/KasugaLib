package kasuga.lib.registrations.common;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class PlacedFeatureReg extends Reg {

    private int groupCountPerChunk = 10;
    private Pair<Integer, Integer> verticalAnchorAbove = null;
    private Pair<Integer, Integer> verticalAnchorAbsolute = null;
    private DistributionType distributionType = DistributionType.TRIANGLE;
    private RegistryObject<ConfiguredFeature<?, ?>> configuredFeatureObject = null;
    private HeightRangePlacement heightRangePlacement = null;
    private RegistryObject<PlacedFeature> object = null;

    public PlacedFeatureReg(String registrationKey) {
        super(registrationKey);
    }

    public PlacedFeatureReg setVerticalAnchorAbove(int maxHeight, int minHeight) {
        this.verticalAnchorAbove = Pair.of(maxHeight, minHeight);
        return this;
    }

    public PlacedFeatureReg setVerticalAnchorAbsolute(int maxHeight, int minHeight) {
        this.verticalAnchorAbsolute = Pair.of(maxHeight, minHeight);
        return this;
    }

    public PlacedFeatureReg setDistributionType(DistributionType distributionType) {
        this.distributionType = distributionType;
        return this;
    }

    public PlacedFeatureReg setGroupCountPerChunk(int count) {
        this.groupCountPerChunk = count;
        return this;
    }

    public PlacedFeatureReg setConfiguredFeatureObject(RegistryObject<ConfiguredFeature<?, ?>> configuredFeature) {
        this.configuredFeatureObject = configuredFeature;
        return this;
    }

    @Override
    public PlacedFeatureReg submit(SimpleRegistry registry) {
        if (verticalAnchorAbove == null && verticalAnchorAbsolute == null) {
            KasugaLib.MAIN_LOGGER.error("In PlacedFeatureReg",
                    new NullPointerException("Neither above nor absolute vertical has been set!")
            );
            crashOnNotPresent(Integer.class, "PlacedFeature", "submit");
            return this;
        }
        VerticalAnchor verticalAnchorTop = null;
        VerticalAnchor verticalAnchorBottom = null;
        if (verticalAnchorAbove != null) {
            verticalAnchorTop = VerticalAnchor.aboveBottom(verticalAnchorAbove.getFirst());
            verticalAnchorBottom = VerticalAnchor.aboveBottom(verticalAnchorAbove.getSecond());
        } else {
            verticalAnchorTop = VerticalAnchor.absolute(verticalAnchorAbsolute.getFirst());
            verticalAnchorBottom = VerticalAnchor.absolute(verticalAnchorAbsolute.getSecond());
        }
        if (distributionType == DistributionType.TRIANGLE) {
            heightRangePlacement = HeightRangePlacement.triangle(verticalAnchorBottom, verticalAnchorTop);
        } else {
            heightRangePlacement = HeightRangePlacement.uniform(verticalAnchorBottom, verticalAnchorTop);
        }

        this.object = registry.placedFeature().register(
                registrationKey,
                () -> new PlacedFeature(
                        configuredFeatureObject.getHolder().get(),
                        commonOrePlacement(groupCountPerChunk, heightRangePlacement)));
        return this;
    }

    public @Nullable RegistryObject<PlacedFeature> getRegisterObject() {
        return this.object;
    }

    @Override
    public String getIdentifier() {
        return "placed_feature";
    }

    private static List<PlacementModifier> orePlacement(PlacementModifier modifier1, PlacementModifier modifier2) {
        return List.of(modifier1, InSquarePlacement.spread(), modifier2, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier modifier2) {
        return orePlacement(CountPlacement.of(count), modifier2);
    }

    private static List<PlacementModifier> rareOrePlacement(int chance, PlacementModifier modifier2) {
        return orePlacement(RarityFilter.onAverageOnceEvery(chance), modifier2);
    }

    public enum DistributionType {
        UNIFORM, TRIANGLE;
    }
}
