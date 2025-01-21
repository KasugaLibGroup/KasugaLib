package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class OreReg<T extends Block> extends Reg {

    private Supplier<T> oreBlockSupplier;
    private final ConfiguredFeatureReg<T> configuredFeatureObject;
    private final PlacedFeatureReg placedFeatureObject;

    public OreReg(String registrationKey) {
        super(registrationKey);
        this.configuredFeatureObject = new ConfiguredFeatureReg<T>(registrationKey);
        this.placedFeatureObject = new PlacedFeatureReg(registrationKey);
    }

    @Mandatory
    public OreReg<T> setOreBlock(Supplier<T> oreBlockSupplier) {
        this.oreBlockSupplier = oreBlockSupplier;
        return this;
    }

    public OreReg<T> addOreBlockReplaceTarget(ResourceLocation block) {
        this.configuredFeatureObject.addOreConfigTargetByBlock(block, oreBlockSupplier);
        return this;
    }

    public OreReg<T> addOreTagReplaceTarget(TagKey<Block> tagKey) {
        this.configuredFeatureObject.addOreConfigTargetByKey(tagKey, oreBlockSupplier);
        return this;
    }

    public OreReg<T> addOreRuleReplaceTarget(RuleTest ruleTest) {
        this.configuredFeatureObject.addOreConfigTargetByRule(ruleTest, oreBlockSupplier);
        return this;
    }

    public OreReg<T> addOreReplaceTarget() {
        this.configuredFeatureObject.addOreConfigTarget(oreBlockSupplier);
        return this;
    }

    public OreReg<T> addDeepSlateReplaceTarget() {
        this.configuredFeatureObject.addDeepSlateOreConfigTarget(oreBlockSupplier);
        return this;
    }

    public OreReg<T> setOreQuantityPerGroup(int count) {
        count = Math.max(1, Math.min(count, 64));
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
        if (configuredFeatureObject == null) {
            crashOnNotPresent(ConfiguredFeatureReg.class, "OrgReg", "submit");
            return this;
        } else if (placedFeatureObject == null) {
            crashOnNotPresent(PlacedFeatureReg.class, "OrgReg", "submit");
            return this;
        }
        configuredFeatureObject.submit(registry);
        placedFeatureObject.setConfiguredFeatureObject(configuredFeatureObject.getRegistryObject()).submit(registry);
        return this;
    }

    @Override
    public String getIdentifier() {
        return "ore";
    }
}
