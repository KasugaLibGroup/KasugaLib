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
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityReg<T extends BlockEntity> extends Reg {
    private ArrayList<BlockProvider<?>> blockInvokerList;
    private RegistryObject<BlockEntityType<T>> registryObject;
    private com.mojang.datafixers.types.Type<?> dataType = null;
    private BlockEntityType.BlockEntitySupplier<T> builder;
    private BlockEntityRendererBuilder<T> rendererBuilder = null;

    public BlockEntityReg(String registrationKey) {
        super(registrationKey);
        blockInvokerList = new ArrayList<>();
    }

    @Mandatory
    public BlockEntityReg<T> blockEntityType(BlockEntityType.BlockEntitySupplier<? extends BlockEntity> blockEntity) {
        this.builder =(BlockEntityType.BlockEntitySupplier<T>) blockEntity;
        return this;
    }

    @Optional
    public BlockEntityReg<T> withBlocks(BlockProvider<?>... block) {
        blockInvokerList.addAll(List.of(block));
        return this;
    }

    @Optional
    public BlockEntityReg<T> addBlock(BlockProvider<?> block) {
        this.blockInvokerList.add(block);
        return this;
    }

    @Optional
    public BlockEntityReg<T> withRenderer(BlockEntityRendererBuilder<?> builder) {
        this.rendererBuilder = (BlockEntityRendererBuilder<T>) builder;
        return this;
    }

    @Optional
    public BlockEntityReg<T> dataType(com.mojang.datafixers.types.Type<?> dataType) {
        this.dataType = dataType;
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


    @Mandatory
    public BlockEntityReg<T> submit(SimpleRegistry registry) {
        registryObject = registry.blockEntity()
                .register(registrationKey, () -> BlockEntityType.Builder.of(builder, getBlockList()).build(dataType));
        return this;
    }

    @Inner
    public void registerRenderer(SimpleRegistry registry) {
        if(this.rendererBuilder != null)
            registry.registerBlockEntityRenderer(() -> registryObject.get(), rendererBuilder);
    }

    public interface BlockProvider<T extends Block> {
        T provide();
    }

    public interface BlockEntityProvider<T extends BlockEntity> {
        BlockEntityType<T> provide();
    }

    public interface BlockEntityRendererBuilder<T extends BlockEntity> {
        BlockEntityRenderer<T> build(BlockEntityRendererProvider.Context context);
    }
}
