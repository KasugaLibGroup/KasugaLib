package kasuga.lib.registrations.create;

import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BogeyGroupReg extends Reg {
    Component translationName = null;
    private final List<BogeyStyleBuilderContext> contexts;
    private NonNullSupplier<BogeyRenderer> defaultRenderer = null;
    private String cycleGroup = "";
    private ParticleOptions contactParticle = null, smokeParticle = null;
    private BogeyStyle style = null;
    /**
     * Use this to create a BlockReg.
     *
     * @param registrationKey your block registration key.
     */
    public BogeyGroupReg(String registrationKey, String cycleGroup) {
        super(registrationKey);
        this.cycleGroup = cycleGroup;
        contexts = new ArrayList<>();
    }

    public BogeyGroupReg translationKey(String key) {
        this.translationName = Component.translatable(key);
        return this;
    }

    public BogeyGroupReg cycleGroup(String group) {
        this.cycleGroup = group;
        return this;
    }

    public BogeyGroupReg defaultRenderer(NonNullSupplier<BogeyRenderer> renderer) {
        this.defaultRenderer = renderer;
        return this;
    }

    public BogeyGroupReg bogeyWithDefaultRenderer(BogeySizes.BogeySize size, ResourceLocation id) {
        contexts.add(new BogeyStyleBuilderContext(size, defaultRenderer, id));
        return this;
    }

    public BogeyGroupReg bogeyWithDefaultRenderer(BogeySizes.BogeySize size, String namespace, BogeyBlockReg<?> reg) {
        contexts.add(new BogeyStyleBuilderContext(size, defaultRenderer, new ResourceLocation(namespace, reg.registrationKey)));
        return this;
    }

    public BogeyGroupReg bogey(BogeySizes.BogeySize size, Supplier<BogeyRenderer> renderer, ResourceLocation id) {
        contexts.add(new BogeyStyleBuilderContext(size, renderer, id));
        return this;
    }

    public BogeyGroupReg bogey(BogeySizes.BogeySize size, Supplier<BogeyRenderer> rendererSupplier, String namespace, BogeyBlockReg<?> reg) {
        contexts.add(new BogeyStyleBuilderContext(size, rendererSupplier, new ResourceLocation(namespace, reg.registrationKey)));
        return this;
    }

    public BogeyGroupReg bogey(BogeySizeReg size, Supplier<BogeyRenderer> rendererSupplier, ResourceLocation id) {
        contexts.add(new BogeyStyleBuilderContext(size.getSize(), rendererSupplier, id));
        return this;
    }

    public BogeyGroupReg bogey(BogeySizeReg size, Supplier<BogeyRenderer> rendererSupplier, String namespace, BogeyBlockReg<?> reg) {
        contexts.add(new BogeyStyleBuilderContext(size.getSize(), rendererSupplier, new ResourceLocation(namespace, reg.registrationKey)));
        return this;
    }

    @Override
    public BogeyGroupReg submit(SimpleRegistry registry) {
        AllBogeyStyles.BogeyStyleBuilder builder= new AllBogeyStyles.BogeyStyleBuilder(registry.asResource(registrationKey), registry.asResource(cycleGroup));
        if (translationName != null) builder.displayName(translationName);
        if (contactParticle != null) builder.contactParticle(contactParticle);
        if (smokeParticle != null) builder.smokeParticle(smokeParticle);
        for (BogeyStyleBuilderContext context : contexts) {
            ResourceLocation location = new ResourceLocation(context.id.getNamespace().equals("") ? registry.namespace : context.id.getNamespace(), context.id.getPath());
            builder.size(context.size, () -> context.supplier, location);
        }
        style = builder.build();
        return this;
    }

    public BogeyStyle getStyle() {
        return style;
    }

    @Override
    public String getIdentifier() {
        return "bogey_group";
    }

    public record BogeyStyleBuilderContext(BogeySizes.BogeySize size, Supplier<BogeyRenderer> supplier, ResourceLocation id){}
}
