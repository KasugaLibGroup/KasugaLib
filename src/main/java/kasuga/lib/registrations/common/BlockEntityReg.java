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
 * 这是方块实体的注册机。方块实体是一个自定义的方块数据容器，你可以在其中自定义方块的行为或渲染。
 * 它的生命周期与方块的生命周期相同。
 * 如果你想存储你的自定义数据或自定义功能，请使用方块实体。
 * 要使用方块实体，你的方块必须是BaseEntityBlock的子类，详见
 * {@link net.minecraft.world.level.block.BaseEntityBlock} 和 {@link BlockEntity} 获取更多信息。
 * @param <T> blockEntity的类型，
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
     * 使用此函数以创建一个方块实体注册机。
     * @param registrationKey 你的方块实体注册机的键。
     * Use this to create a block entity reg.
     * @param registrationKey the registration key of your block entity
     */
    public BlockEntityReg(String registrationKey) {
        super(registrationKey);
        blockInvokerList = new ArrayList<>();
    }

    /**
     * 这个是你方块实体的实例提供者。它用于为游戏提供一个方块实体实例。
     * @param blockEntity 方块实体提供者。
     * @return 自身
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
     * 哪些方块可以拥有这个方块实体？如果方块实体所在位置的方块无效，方块实体将被销毁。
     * @param block block suppliers.
     * @return 自身
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
     * 将所有满足你需求的方块链接到这个blockEntity。
     * @param predicate 你的需求。
     * @return 自身
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
     * 将一个方块包链接到这个方块实体。
     * @param bundle 你的方块包。
     * @return 自身
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
     * 添加一个方块到方块实体的有效方块列表。
     * @param block 方块提供者。
     * @return 自身
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
     * 这个方块实体可能有一个方块实体渲染器。
     * 方块实体渲染器用于自定义方块渲染。
     * 它仅在客户端应用。
     * @param builder 你的方块实体渲染器提供者。
     * @return 自身
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
     * 其他你想应用到这个方块实体的原版数据。
     * @param dataType 你想提供的数据。
     * @return 自身
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
     * 你必须在配置完成后调用此方法。它会处理你的注册到forge和minecraft。
     * @param registry mod的SimpleRegistry。
     * @return 自身
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
     * 一个函数接口，提供一个方块。重要的是，你必须从一个有效的注册对象中获取你的方块，比如Kasuga Reg中的BlockReg或者minecraftforge中的RegistryObject。
     * 游戏需要有自己注册数据的方块。如果你只是通过调用一个新实例来提供这个。
     * 我们可以写"() -> exampleBlockReg.instance()"或者"exampleBlockReg::get"来获取它。更多信息，见{@link BlockReg#instance()}和{@link RegistryObject#get()}
     * @param <T> 你的方块的类型。
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
     * 一个函数接口，提供一个方块实体。它是你的方块实体的初始化器。在这里传递一个构造方法，比如"ExampleBlockEntity::new"或者"() -> new ExampleBlockEntity()"。
     * @param <T> 你的方块实体的类型。
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
