package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.registrations.BlockEntityRendererBuilder;
import kasuga.lib.registrations.BundledReg;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The registration element of block entity. Block entity is a custom block data container, which makes us can
 * customize the block action or renderer. It is spawn and destroy as same as the block's lifecycle.
 * If you want to storage your custom data or your custom function, use a block entity. To use a block entity,
 * Your block must be a subClass of BaseEntityBlock, See
 * {@link net.minecraft.world.level.block.BaseEntityBlock} and {@link BlockEntity} for more info.
 * @param <T> the type of blockEntity,
 */
public class BlockEntityReg<T extends BlockEntity> extends Reg {
    private ArrayList<BlockProvider<?>> blockInvokerList;
    private RegistryObject<BlockEntityType<T>> registryObject;
    private com.mojang.datafixers.types.Type<?> dataType = null;
    private BlockEntityType.BlockEntitySupplier<T> builder;
    private Supplier<BlockEntityRendererBuilder<T>> rendererBuilder = null;
    private BiPredicate<ResourceLocation, Block> blockPredicate = null;

    /**
     * Use this to create a block entity reg.
     * @param registrationKey the registration key of your block entity
     */
    public BlockEntityReg(String registrationKey) {
        super(registrationKey);
        blockInvokerList = new ArrayList<>();
    }

    /**
     * The instance supplier of your block entity. It is used to provide a block entity instance for the game.
     * @param blockEntity Your Block Entity supplier.
     * @return self.
     */
    @Mandatory
    public BlockEntityReg<T> blockEntityType(BlockEntityType.BlockEntitySupplier blockEntity) {
        this.builder =(BlockEntityType.BlockEntitySupplier<T>) blockEntity;
        return this;
    }

    /**
     * Which blocks could have this block entity ? Block Entity would be destroyed if
     * there's an invalid block in its block pos.
     * @param block The block suppliers.
     * @return self.
     */
    @Optional
    public BlockEntityReg<T> withBlocks(BlockProvider<?>... block) {
        blockInvokerList.addAll(List.of(block));
        return this;
    }


    /**
     * Link all blocks that satisfied your predication to this blockEntity.
     * @param predicate your predication.
     * @return self.
     */
    @Optional
    public BlockEntityReg<T> blockPredicates(BiPredicate<ResourceLocation, Block> predicate) {
        this.blockPredicate = predicate;
        return this;
    }

    /**
     * Link a bundle of block to this blockEntity.
     * @param bundle your block bundle.
     * @return self.
     */
    @Optional
    public BlockEntityReg<T> blockBundle(BundledReg<? extends BlockReg<?>> bundle) {
        for (BlockReg<?> blockReg : bundle.getElements().values())
            blockInvokerList.add(blockReg::getBlock);
        return this;
    }


    /**
     * Aaa a block to block entity's valid block list.
     * @param block the block supplier.
     * @return self
     */
    @Optional
    public BlockEntityReg<T> addBlock(BlockProvider<?> block) {
        this.blockInvokerList.add(block);
        return this;
    }

    public BlockEntityReg<T> addBlock(BlockReg<?> reg) {
        this.blockInvokerList.add(reg::getBlock);
        return this;
    }

    /**
     * The block entity may have a block entity renderer.
     * Block entity renderer is used for customize block rendering.
     * It would only be applied in client side.
     * @param builder your block entity renderer supplier.
     * @return self.
     */
    @Optional
    public BlockEntityReg<T> withRenderer(Supplier<BlockEntityRendererBuilder<T>> builder) {
        this.rendererBuilder = builder;
        return this;
    }

    /**
     * Other vanilla data you would like to apply to this block entity.
     * @param dataType data you would like to give.
     * @return self.
     */
    @Optional
    public BlockEntityReg<T> dataType(com.mojang.datafixers.types.Type<?> dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * You must call this after your config completed. It would handle your reg to forge and minecraft.
     * @param registry the mod SimpleRegistry.
     * @return self
     */
    @Mandatory
    public BlockEntityReg<T> submit(SimpleRegistry registry) {
        if (builder == null) {
            crashOnNotPresent(BlockEntityType.BlockEntitySupplier.class, "blockEntityType", "submit");
        }
        registryObject = registry.blockEntity()
                .register(registrationKey, () -> BlockEntityType.Builder.of(builder, getBlockList()).build(dataType));
        if (rendererBuilder != null)
            registry.cacheBeIn(this);
        return this;
    }

    public Block[] getBlockList() {
        LinkedList<Block> castBlockList = new LinkedList<>();
        if (blockPredicate != null) {
            for (Map.Entry<ResourceKey<Block>, Block> entries : ForgeRegistries.BLOCKS.getEntries()) {
                if (blockPredicate.test(entries.getKey().location(), entries.getValue()))
                    castBlockList.add(entries.getValue());
            }
        }
        Block[] result = new Block[blockInvokerList.size() + castBlockList.size()];
        int counter = 0;
        if (blockPredicate != null) {
            for (Block i : castBlockList) {
                result[counter] = i;
                counter++;
            }
        }
        for(BlockProvider<?> provider : blockInvokerList) {
            result[counter] = provider.provide();
            counter++;
        }

        return result;
    }

    public com.mojang.datafixers.types.Type<?> getData() {
        return dataType;
    }

    public BlockEntityRendererBuilder<T> getRendererBuilder() {
        return rendererBuilder.get();
    }

    public BlockEntityType<T> getType() {
        return registryObject == null ? null : registryObject.get();
    }

    public String getIdentifier() {
        return "block_entity";
    }

    @Inner
    public void registerRenderer(SimpleRegistry registry) {
        if(this.rendererBuilder != null)
            registry.registerBlockEntityRenderer(() -> registryObject.get(), rendererBuilder.get());
    }

    /**
     * A function interface that provides a block. It is important that you must get your block from an vaild
     * registration object like BlockReg in Kasuga Reg or RegistryObject in minecraftforge.
     * The game needs blocks that has their own registration data. If you just provide this by calling a new instance
     * For example, we could write "() -> exampleBlockReg.instance()" or "exampleBlockReg::get"
     * to get this. For more info, see {@link BlockReg#instance()} and {@link RegistryObject#get()}
     * @param <T> Type of your block.
     */
    public interface BlockProvider<T extends Block> {
        T provide();
    }

    /**
     * A function interface that provides a bLock entity. It is a initializer of your block entity. Pass a
     * constructor method in this, like "ExampleBlockEntity::new" or "() -> new ExampleBlockEntity()".
     * @param <T> Type of your block entity.
     */
    public interface BlockEntityProvider<T extends BlockEntity> {
        BlockEntityType<T> provide();
    }

    public interface BiPredicate<T, K> {
        boolean test(T first, K second);
    }
}
