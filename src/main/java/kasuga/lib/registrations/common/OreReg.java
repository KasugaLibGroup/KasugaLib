package kasuga.lib.registrations.common;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class OreReg<T extends Block> extends Reg {

    private ConfiguredFeatureReg<T> configuredFeatureObject = null;

    private PlacedFeatureReg placedFeatureObject = null;

    public OreReg(String registrationKey) {
        super(registrationKey);
        this.configuredFeatureObject = new ConfiguredFeatureReg<T>(registrationKey);
        this.placedFeatureObject = new PlacedFeatureReg(registrationKey + "_placed");
    }

    public OreReg<T> setOreBlock(Supplier<T> oreBlock) {
        this.configuredFeatureObject.addOreConfigTarget(oreBlock);
        return this;
    }

    public OreReg<T> addOreBlockReplaceTarget(Block block, Supplier<T> ore) {
        this.configuredFeatureObject.addOreConfigTarget(block, ore);
        return this;
    }

    public OreReg<T> addOreTagReplaceTarget(TagKey<Block> tagKey, Supplier<T> ore) {
        this.configuredFeatureObject.addOreConfigTarget(tagKey, ore);
        return this;
    }

    public OreReg<T> addOreRuleReplaceTarget(RuleTest ruleTest, Supplier<T> ore) {
        this.configuredFeatureObject.addOreConfigTarget(ruleTest, ore);
        return this;
    }

    public OreReg<T> setOreQuantityPerGroup(int count) {
        this.configuredFeatureObject.setQuantityPerGroup(count);
        return this;
    }

    public OreReg<T> setOreAnchorAbove(int maxHeight, int minHeight) {
        this.placedFeatureObject.setVerticalAnchorAbove(maxHeight, minHeight);
        return this;
    }

    public OreReg<T> setOreAnchorAbsolute(int maxHeight, int minHeight) {
        this.placedFeatureObject.setVerticalAnchorAbsolute(maxHeight, minHeight);
        return this;
    }

    public OreReg<T> setOreDistributionType(PlacedFeatureReg.DistributionType distributionType) {
        this.placedFeatureObject.setDistributionType(distributionType);
        return this;
    }

    public OreReg<T> setOreCountPerChunk(int count) {
        this.placedFeatureObject.setGroupCountPerChunk(count);
        return this;
    }

    @Override
    public OreReg<T> submit(SimpleRegistry registry) {
        if (configuredFeatureObject == null || placedFeatureObject == null)
            throw new NullPointerException("ConfiguredFeature or placedFeature is null in OreReg!");
        configuredFeatureObject.submit(registry);
        placedFeatureObject.setConfiguredFeatureObject(configuredFeatureObject.getRegistryObject());
        placedFeatureObject.submit(registry);
        return this;
    }

    @Override
    public String getIdentifier() {
        return "ore";
    }
}
