package kasuga.lib.core.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import kasuga.lib.registrations.common.BlockReg;
import kasuga.lib.registrations.common.ItemReg;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BlockStateGenerator<T extends AbstractBogeyBlock<?>> {
    private final List<BlockReg.PropertyIdentifier> identifiers;
    private final NonNullSupplier<Block> initBlock;
    private HarvestProperty harvest = HarvestProperty.PICKAXE;
    private String modelPath = "";
    private Supplier<ItemLike> dropItem = AllBlocks.RAILWAY_CASING::get;

    public BlockStateGenerator(NonNullSupplier<Block> initialBlock) {
        initBlock = initialBlock;
        identifiers = new ArrayList<>();
    }

    public BlockStateGenerator(BlockReg<?> initialBlock) {
        this(initialBlock::getBlock);
    }

    public BlockStateGenerator(RegistryObject<? extends Block> registration) {
        this(registration::get);
    }

    public BlockStateGenerator(BlockEntry<?> entry) {
        this(entry::get);
    }

    public static <T extends AbstractBogeyBlock<?>> BlockStateGenerator<T> ofWooden() {
        return new BlockStateGenerator(SharedProperties::wooden);
    }

    public static <T extends AbstractBogeyBlock<?>> BlockStateGenerator<T> ofStone() {
        return new BlockStateGenerator(SharedProperties::stone);
    }

    public static <T extends AbstractBogeyBlock<?>> BlockStateGenerator<T> ofSoftMetal() {
        return new BlockStateGenerator(SharedProperties::softMetal);
    }

    public static <T extends AbstractBogeyBlock<?>> BlockStateGenerator<T> ofCopperMetal() {
        return new BlockStateGenerator(SharedProperties::copperMetal);
    }

    public static <T extends AbstractBogeyBlock<?>> BlockStateGenerator<T> ofNetheriteMetal() {
        return new BlockStateGenerator(SharedProperties::netheriteMetal);
    }

    public BlockStateGenerator<T> sound(SoundType soundType) {
        identifiers.add(p -> p.sound(soundType));
        return this;
    }

    public BlockStateGenerator<T> noOcclusion() {
        identifiers.add(p -> p.noOcclusion());
        return this;
    }

    public BlockStateGenerator<T> pickaxeOnly() {
        harvest = HarvestProperty.PICKAXE;
        return this;
    }

    public BlockStateGenerator<T> axeOnly() {
        harvest = HarvestProperty.AXE;
        return this;
    }

    public BlockStateGenerator<T> pickaxeOrAxe() {
        harvest = HarvestProperty.ALL;
        return this;
    }

    public BlockStateGenerator<T> modelPath(String path) {
        this.modelPath = path;
        return this;
    }

    public BlockStateGenerator<T> dropItem(Supplier<ItemLike> drop) {
        this.dropItem = drop;
        return this;
    }

    public BlockStateGenerator<T> dropItem(BlockReg<?> drop) {
        this.dropItem = drop::getBlock;
        return this;
    }

    public BlockStateGenerator<T> dropBlockItem(BlockReg<?> drop) {
        this.dropItem = drop::itemInstance;
        return this;
    }

    public BlockStateGenerator<T> dropItem(RegistryObject<? extends ItemLike> reg) {
        this.dropItem = reg::get;
        return this;
    }

    public BlockStateGenerator<T> dropItem(ItemReg<?> drop) {
        this.dropItem = drop::getItem;
        return this;
    }

    public <B extends T, P> NonNullUnaryOperator<BlockBuilder<B, P>> build() {
        return b -> {
            b.initialProperties(initBlock);
            identifiers.forEach(identifier -> b.properties(p -> {identifier.apply(p);return p;}));
            switch (harvest) {
                case ALL -> b.transform(TagGen.pickaxeOnly());
                case AXE -> b.transform(TagGen.axeOnly());
                case PICKAXE -> b.transform(TagGen.pickaxeOnly());
            }
            b.blockstate((c, p) -> BlockStateGen.horizontalAxisBlock(c, p, s -> p.models().getExistingFile(p.modLoc(modelPath))));
            b.loot((p, l) -> p.dropOther(l, dropItem.get()));
            return b;
        };
    }

    public enum HarvestProperty {
        PICKAXE, AXE, ALL;
    }
}
