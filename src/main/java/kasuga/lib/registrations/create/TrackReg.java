package kasuga.lib.registrations.create;

import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockItem;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import kasuga.lib.core.create.TrackStateGenerator;
import kasuga.lib.registrations.common.BlockReg;
import kasuga.lib.registrations.common.CreativeTabReg;
import kasuga.lib.registrations.registry.CreateRegistry;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TrackReg<T extends TrackBlock> extends BlockReg<T> {
    private BlockEntry<T> entry;
    private Supplier<TrackMaterial> trackMaterialSupplier;
    private List<TagKey<Block>> tags;
    private NonNullSupplier<CreativeTabReg> reg;
    private TrackStateGenerator.Builder generator;
    private ResourceLocation trackItemModelLocation;
    int transformType = 0;
    private String trackNameSuffix = "";
    private BlockReg.BlockRendererBuilder<T> rendererBuilder;

    /**
     * Use this to create a BlockReg.
     *
     * @param registrationKey your block registration key.
     */
    public TrackReg(String registrationKey) {
        super(registrationKey);
        tags = new ArrayList<>();
    }

    public TrackReg<T> addTags(TagKey<Block> tag) {
        tags.add(tag);
        return this;
    }

    public TrackReg<T> tab(CreativeTabReg tabReg) {
        this.reg = () -> tabReg;
        return this;
    }

    public TrackReg<T> trackMaterial(Supplier<TrackMaterial> trackMaterialSupplier) {
        this.trackMaterialSupplier = trackMaterialSupplier;
        return this;
    }

    public TrackReg<T> trackState(TrackStateGenerator.Builder builder) {
        this.generator = builder;
        return this;
    }

    public TrackReg<T> trackNameSuffix(String suffix) {
        this.trackNameSuffix = suffix;
        return this;
    }

    public TrackReg<T> pickaxeOnly() {
        transformType = 0;
        return this;
    }

    public TrackReg<T> axeOnly() {
        transformType = 1;
        return this;
    }

    public TrackReg<T> axeOrPickaxe() {
        transformType = 2;
        return this;
    }

    public TrackReg<T> withBlockRenderer(BlockRendererBuilder<T> builder) {
        this.rendererBuilder = builder;
        return this;
    }

    @Override
    public TrackReg<T> submit(SimpleRegistry registry) {
        if (!(registry instanceof CreateRegistry createRegistry)) return this;
        initProperties();
        CreateRegistrate registrate = createRegistry.createRegistry();
        if (reg != null) registrate = registrate.setCreativeTab(reg.get().getTabRegistryObject());
        TrackMaterial material = trackMaterialSupplier.get();
        com.tterrag.registrate.builders.BlockBuilder<T, CreateRegistrate> builder =
                (com.tterrag.registrate.builders.BlockBuilder<T, CreateRegistrate>)
                        registrate.block(registrationKey, material::createBlock)
                .initialProperties(SharedProperties::copperMetal);
        switch (transformType) {
            case 1 -> builder.transform(TagGen.axeOnly());
            case 2 -> builder.transform(TagGen.axeOrPickaxe());
            default -> builder.transform(TagGen.pickaxeOnly());
        }
        builder
                .blockstate(generator.build()::generate)
                .lang(material.langName + trackNameSuffix)
                .properties(BlockBehaviour.Properties::noOcclusion);
        tags.forEach(builder::tag);
        builder.onRegister(CreateRegistrate.blockModel(()->TrackModelDeduplicator.simple(TrackModel::new)))
                .item(TrackBlockItem::new)
                .model((c, p) -> p.generated(c, trackItemModelLocation)).build();
        entry = builder.register();
        reg.get().item(() -> entry.asItem());
        return this;
    }

    public BlockEntry<T> getEntry() {
        return entry;
    }

    public T getTrackBlock() {
        return entry.get();
    }

    public Item getTrackItem() {
        return entry.get().asItem();
    }

    @Override
    public T getBlock() {
        return getEntry().get();
    }

    @Override
    public String getIdentifier() {
        return "track";
    }
}
