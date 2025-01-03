package kasuga.lib.registrations.common;

import com.google.common.base.Suppliers;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class ConfiguredFeatureReg<T extends Block> extends Reg {

    private int quantityPerGroup = 9;

    private List<OreConfiguration.TargetBlockState> oreConfigTargetList = List.of();

    private Supplier<List<OreConfiguration.TargetBlockState>> oreConfigurationListSupplier =
            Suppliers.memoize(() -> oreConfigTargetList);

    private RegistryObject<ConfiguredFeature<?, ?>> registryObject = null;

    public ConfiguredFeatureReg(String registrationKey) {
        super(registrationKey);
    }

    public ConfiguredFeatureReg<T> addOreConfigTarget(T ore) {
        oreConfigTargetList.add(OreConfiguration.target(
                OreFeatures.STONE_ORE_REPLACEABLES,
                ore.defaultBlockState()));
        return this;
    }

    public ConfiguredFeatureReg<T> addOreConfigTarget(RuleTest replacedBlockRuleTest, Supplier<T> ore) {
        oreConfigTargetList.add(OreConfiguration.target(
                replacedBlockRuleTest,
                ore.get().defaultBlockState()));
        return this;
    }

    public ConfiguredFeatureReg<T> addOreConfigTarget(TagKey<Block> blockTagKey, Supplier<T> ore) {
        oreConfigTargetList.add(OreConfiguration.target(
                new TagMatchTest(blockTagKey),
                ore.get().defaultBlockState()));
        return this;
    }

    public ConfiguredFeatureReg<T> addOreConfigTarget(Block block, Supplier<T> ore) {
        oreConfigTargetList.add(OreConfiguration.target(
                new BlockMatchTest(block),
                ore.get().defaultBlockState()));
        return this;
    }

    public ConfiguredFeatureReg<T> setQuantityPerGroup(int count) {
        this.quantityPerGroup = count;
        return this;
    }

    @Override
    public ConfiguredFeatureReg<T> submit(SimpleRegistry registry) {
        registryObject = registry.configuredFeature().register(
                registrationKey,
                () -> new ConfiguredFeature<>(
                        Feature.ORE,
                        new OreConfiguration(oreConfigurationListSupplier.get(), quantityPerGroup)
                ));
        return this;
    }

    @Override
    public String getIdentifier() {
        return "configured_feature";
    }

    public int getQuantityPerGroup() {
        return this.quantityPerGroup;
    }

    public List<OreConfiguration.TargetBlockState> getOreConfigTargetList() {
        return this.oreConfigTargetList;
    }

    public Supplier<List<OreConfiguration.TargetBlockState>> getOreConfigurationListSupplier() {
        return this.oreConfigurationListSupplier;
    }

    public RegistryObject<ConfiguredFeature<?, ?>> getRegistryObject() {
        return this.registryObject;
    }
}
