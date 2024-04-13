package kasuga.lib.registrations.create;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import kasuga.lib.core.create.BlockStateGenerator;
import kasuga.lib.core.create.TrackStateGenerator;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.common.BlockReg;
import kasuga.lib.registrations.registry.CreateRegistry;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.List;

public class BogeyBlockReg<T extends AbstractBogeyBlock<?>> extends Reg {
    private final List<BlockReg.PropertyIdentifier> identifiers;
    private BogeyBlockBuilder<T> builder = null;
    private NonNullSupplier<BogeySizes.BogeySize> size = null;
    private MapColor color = MapColor.NONE;
    private BlockStateGenerator<T> stateGenerator = null;
    private String langKey = "";
    private BlockEntry<T> entry = null;

    public BogeyBlockReg(String registrationKey) {
        super(registrationKey);
        this.identifiers = new ArrayList<>();
    }

    public BogeyBlockReg<T> block(BogeyBlockBuilder<T> builder) {
        this.builder = builder;
        return this;
    }

    public BogeyBlockReg<T> materialColor(MapColor color) {
        this.color = color;
        return this;
    }

    public BogeyBlockReg<T> translationKey(String key) {
        this.langKey = key;
        return this;
    }

    public BogeyBlockReg<T> size(NonNullSupplier<BogeySizes.BogeySize> size) {
        this.size = size;
        return this;
    }

    public BogeyBlockReg<T> size(BogeySizeReg sizeReg) {
        this.size = sizeReg::getSize;
        return this;
    }

    public BogeyBlockReg<T> transform(BlockStateGenerator<T> generator) {
        this.stateGenerator = generator;
        return this;
    }

    public BogeyBlockReg<T> property(BlockReg.PropertyIdentifier identifier) {
        this.identifiers.add(identifier);
        return this;
    }

    public BogeyBlockReg<T> noOcclusion() {
        this.identifiers.add(BlockBehaviour.Properties::noOcclusion);
        return this;
    }

    @Override
    public BogeyBlockReg<T> submit(SimpleRegistry registry) {
        if (!(registry instanceof CreateRegistry createRegistry)) return this;
        BlockBuilder<T, CreateRegistrate> blockBuilder =
                createRegistry.createRegistry().block(registrationKey, p -> builder.get(p, size.get()))
                        .initialProperties(SharedProperties::stone)
                        .properties(p -> p.mapColor(color));
        if (stateGenerator != null)
            blockBuilder.transform(stateGenerator.build());
        blockBuilder.lang(langKey).properties(BlockBehaviour.Properties::noOcclusion);
        identifiers.forEach(i -> blockBuilder.properties(p -> {i.apply(p);return p;}));
        entry = blockBuilder.register();
        return this;
    }

    public BlockEntry<T> getEntry() {
        return entry;
    }

    @Override
    public String getIdentifier() {
        return "bogey_block";
    }

    public interface BogeyBlockBuilder<T extends AbstractBogeyBlock<?>> {
        T get(BlockBehaviour.Properties properties, BogeySizes.BogeySize size);
    }
}
