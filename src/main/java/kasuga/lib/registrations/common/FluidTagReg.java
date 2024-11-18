package kasuga.lib.registrations.common;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.TagReg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class FluidTagReg extends TagReg<Fluid> {

    private TagKey<Fluid> tagKey;

    private final String path;

    public FluidTagReg(String registrationKey, String path) {
        super(registrationKey, path);
        this.path = path;
    }

    public FluidTagReg(String namespace, String registrationKey, String path) {
        super(namespace, registrationKey, path);
        this.path = path;
    }

    @Override
    public TagKey<Fluid> tag() {
        return tagKey;
    }

    @Override
    public Reg submit(SimpleRegistry registry) {
        location = otherNamespace == null ?
                new ResourceLocation(registry.namespace, path) :
                new ResourceLocation(otherNamespace, path);
        tagKey = FluidTags.create(location);
        return this;
    }

    @Override
    public String getIdentifier() {
        return "fluid_tag";
    }
}
