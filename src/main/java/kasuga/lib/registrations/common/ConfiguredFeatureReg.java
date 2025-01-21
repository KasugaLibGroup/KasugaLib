package kasuga.lib.registrations.common;

import com.google.common.base.Suppliers;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ConfiguredFeatureReg<T extends Block> extends Reg {

    private int quantityPerGroup = 9;

    private final List<Supplier<OreConfiguration.TargetBlockState>> oreConfigTargetList;
    private RegistryObject<ConfiguredFeature<?, ?>> registryObject = null;

    public ConfiguredFeatureReg(String registrationKey) {
        super(registrationKey);
        this.oreConfigTargetList = new ArrayList<>();
    }

    public ConfiguredFeatureReg<T> addOreConfigTarget(BlockReg<T> ore) {
        return this.addOreConfigTarget(ore::getBlock);
    }

    public ConfiguredFeatureReg<T> addOreConfigTarget(Supplier<T> ore) {
        oreConfigTargetList.add(() -> OreConfiguration.target(
                OreFeatures.STONE_ORE_REPLACEABLES,
                ore.get().defaultBlockState()));
        return this;
    }

    public ConfiguredFeatureReg<T> addDeepSlateOreConfigTarget(BlockReg<T> ore) {
        return this.addDeepSlateOreConfigTarget(ore::getBlock);
    }

    public ConfiguredFeatureReg<T> addDeepSlateOreConfigTarget(Supplier<T> ore) {
        oreConfigTargetList.add(() -> OreConfiguration.target(
                OreFeatures.DEEPSLATE_ORE_REPLACEABLES,
                ore.get().defaultBlockState()
        ));
        return this;
    }

    public ConfiguredFeatureReg<T> addOreConfigTargetByRule(RuleTest replacedBlockRuleTest, Supplier<T> ore) {
        oreConfigTargetList.add(() -> OreConfiguration.target(
                replacedBlockRuleTest,
                ore.get().defaultBlockState()));
        return this;
    }

    public ConfiguredFeatureReg<T> addOreConfigTargetByKey(TagKey<Block> blockTagKey, Supplier<T> ore) {
        oreConfigTargetList.add(() -> OreConfiguration.target(
                new TagMatchTest(blockTagKey),
                ore.get().defaultBlockState()));
        return this;
    }

    public ConfiguredFeatureReg<T> addOreConfigTargetByBlock(ResourceLocation block, Supplier<T> ore) {
        oreConfigTargetList.add(() -> {
            Block b = ForgeRegistries.BLOCKS.getValue(block);
            if (b == null) return null;
            return OreConfiguration.target(
            new BlockMatchTest(b),
            ore.get().defaultBlockState());
        });
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
                        new OreConfiguration(getOreConfigTargetList(), quantityPerGroup)));
        return this;
    }

    @Override
    public String getIdentifier() {
        return "configured_feature";
    }

    public int getQuantityPerGroup() {
        return this.quantityPerGroup;
    }

    public List<Supplier<OreConfiguration.TargetBlockState>> getOreConfigTargetSupplierList() {
        return this.oreConfigTargetList;
    }

    public List<OreConfiguration.TargetBlockState> getOreConfigTargetList() {
        List<OreConfiguration.TargetBlockState> list = new ArrayList<>(oreConfigTargetList.size());
        oreConfigTargetList.forEach(sup -> {
            if (sup == null) return;
            list.add(sup.get());
        });
        return list;
    }

    public RegistryObject<ConfiguredFeature<?, ?>> getRegistryObject() {
        return this.registryObject;
    }
}
