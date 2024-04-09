package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

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
    private BlockEntityRendererBuilder<T> rendererBuilder = null;

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
    public BlockEntityReg<T> blockEntityType(BlockEntityType.BlockEntitySupplier<? extends BlockEntity> blockEntity) {
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
     * Aaa a block to block entity's valid block list.
     * @param block the block supplier.
     * @return self
     */
    @Optional
    public BlockEntityReg<T> addBlock(BlockProvider<?> block) {
        this.blockInvokerList.add(block);
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
    public BlockEntityReg<T> withRenderer(BlockEntityRendererBuilder<?> builder) {
        this.rendererBuilder = (BlockEntityRendererBuilder<T>) builder;
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
        return this;
    }

    public Block[] getBlockList() {
        Block[] result = new Block[blockInvokerList.size()];
        int counter = 0;
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
        return rendererBuilder;
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
            registry.registerBlockEntityRenderer(() -> registryObject.get(), rendererBuilder);
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

    /**
     * A function interface that provides a block entity renderer. If you want to use a block entity renderer on your block and
     * block entity, you should first override the {@link Block#getRenderShape(BlockState)} method under your block class.
     * Then, pass a block entity renderer builder into {@link BlockEntityReg#withRenderer(BlockEntityRendererBuilder)} method.
     * The lib would deal with it for you.
     * @param <T> the block entity your renderer belongs to.
     */
    public interface BlockEntityRendererBuilder<T extends BlockEntity> {
        BlockEntityRenderer<T> build(BlockEntityRendererProvider.Context context);
    }
}
