package kasuga.lib.registrations.create;

import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockItem;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackModel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import kasuga.lib.core.create.BlockStateGenerator;
import kasuga.lib.core.create.TrackStateGenerator;
import kasuga.lib.registrations.common.BlockReg;
import kasuga.lib.registrations.common.CreativeTabReg;
import kasuga.lib.registrations.registry.CreateRegistry;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TrackReg<T extends TrackBlock> extends BlockReg<T> {
    private BlockEntry<T> entry;
    private Supplier<TrackMaterial> trackMaterialSupplier;
    private List<TagKey<Block>> tags;
    private NonNullSupplier<CreativeModeTab> tabSupplier;
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

    public TrackReg<T> tab(NonNullSupplier<CreativeModeTab> tab) {
        this.tabSupplier = tab;
        return this;
    }

    public TrackReg<T> tab(CreativeTabReg tabReg) {
        this.tabSupplier = tabReg::getTab;
        return this;
    }

    public TrackReg<T> material(Material material) {
        super.material(material);
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
        if (tabSupplier != null) registrate = registrate.creativeModeTab(tabSupplier);
        TrackMaterial material = trackMaterialSupplier.get();
        com.tterrag.registrate.builders.BlockBuilder<T, CreateRegistrate> builder =
                (com.tterrag.registrate.builders.BlockBuilder<T, CreateRegistrate>)
                        registrate.block(registrationKey, material::createBlock)
                .initialProperties(super.material);
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
        if (rendererBuilder != null) registry.cacheBlockRendererIn(this, rendererBuilder);
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
