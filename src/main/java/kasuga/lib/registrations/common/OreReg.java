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

import java.util.HashMap;
import java.util.function.Supplier;

public class OreReg<T extends Block> extends Reg {
    private final ConfiguredFeatureReg<T> configuredFeatureObject;
    private final PlacedFeatureReg placedFeatureObject;

    public OreReg(String registrationKey) {
        super(registrationKey);
        this.configuredFeatureObject = new ConfiguredFeatureReg<T>(registrationKey);
        this.placedFeatureObject = new PlacedFeatureReg(registrationKey);
    }

    public OreReg<T> addOreBlockReplaceTarget(ResourceLocation block, Supplier<T> supplier) {
        this.configuredFeatureObject.addOreConfigTargetByBlock(block, supplier);
        return this;
    }

    public OreReg<T> addOreBlockReplaceTarget(ResourceLocation block, BlockReg<T> reg) {
        return addOreBlockReplaceTarget(block, reg::getBlock);
    }

    public OreReg<T> addOreTagReplaceTarget(TagKey<Block> tagKey, Supplier<T> supplier) {
        this.configuredFeatureObject.addOreConfigTargetByKey(tagKey, supplier);
        return this;
    }

    public OreReg<T> addOreTagReplaceTarget(TagKey<Block> tagKey, BlockReg<T> reg) {
        return addOreTagReplaceTarget(tagKey, reg::getBlock);
    }

    public OreReg<T> addOreRuleReplaceTarget(RuleTest rule, Supplier<T> supplier) {
        this.configuredFeatureObject.addOreConfigTargetByRule(rule, supplier);
        return this;
    }

    public OreReg<T> addOreRuleReplaceTarget(RuleTest rule, BlockReg<T> reg) {
        return addOreRuleReplaceTarget(rule, reg::getBlock);
    }

    public OreReg<T> addOreReplaceTarget(Supplier<T> block) {
        this.configuredFeatureObject.addOreConfigTarget(block);
        return this;
    }

    public OreReg<T> addOreReplaceTarget(BlockReg<T> reg) {
        return addOreReplaceTarget(reg::getBlock);
    }

    public OreReg<T> addDeepSlateReplaceTarget(Supplier<T> block) {
        this.configuredFeatureObject.addDeepSlateOreConfigTarget(block);
        return this;
    }

    public OreReg<T> addDeepSlateReplaceTarget(BlockReg<T> reg) {
        return addDeepSlateReplaceTarget(reg::getBlock);
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
