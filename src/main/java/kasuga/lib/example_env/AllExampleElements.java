package kasuga.lib.example_env;

import kasuga.lib.KasugaLib;
import kasuga.lib.example_env.block.GreenAppleBlock;
import kasuga.lib.example_env.block.GreenAppleItem;
import kasuga.lib.example_env.block_entity.GreenAppleTile;
import kasuga.lib.example_env.client.block_entity.renderer.GreenAppleTileRenderer;
import kasuga.lib.example_env.client.entity.renderer.WuLingRenderer;
import kasuga.lib.example_env.entity.WuLingEntity;
import kasuga.lib.example_env.fluid.ExampleFluid;
import kasuga.lib.example_env.fluid.ExampleFluidBlock;
import kasuga.lib.example_env.network.ExampleC2SPacket;
import kasuga.lib.example_env.network.ExampleS2CPacket;
import kasuga.lib.registrations.registry.SimpleRegistry;
import kasuga.lib.registrations.client.ModelReg;
import kasuga.lib.registrations.common.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class AllExampleElements {

    public static final SimpleRegistry testRegistry = new SimpleRegistry(KasugaLib.MOD_ID, KasugaLib.EVENTS);

    public static final BlockReg<GreenAppleBlock> greenApple = new BlockReg<GreenAppleBlock>("green_apple")
            .blockType(GreenAppleBlock::new)
            .material(Material.AIR)
            .materialColor(MaterialColor.COLOR_GREEN)
            .withSound(SoundType.CROP)
            .withBlockEntity("green_apple_tile", GreenAppleTile::new)
            .withBlockEntityRenderer(GreenAppleTileRenderer::new)
            .defaultBlockItem(new ResourceLocation(KasugaLib.MOD_ID, "block/test/green_apple"))
            .stackSize(32)
            .tabTo(CreativeModeTab.TAB_DECORATIONS)
            .submit(testRegistry);

    public static final EntityReg<WuLingEntity> wuling = new EntityReg<WuLingEntity>("wuling")
            .entityType(WuLingEntity::new)
            .size(3, 3)
            .attribute(WuLingEntity::createAttributes)
            .withRenderer(WuLingRenderer::new)
            .submit(testRegistry);

    public static final ModelReg greenAppleModel = new ModelReg("green_apple", new ResourceLocation(KasugaLib.MOD_ID, "block/test/green_apple"))
            .submit(testRegistry);

    public static final ModelReg wuLingVans = new
            ModelReg("wuling_vans", new ResourceLocation(KasugaLib.MOD_ID, "entity/test/wuling/wuling_base"))
            .submit(testRegistry);

    public static final ItemReg<GreenAppleItem> greenAppleItem = new ItemReg<GreenAppleItem>("green_apple_item")
            .itemType(GreenAppleItem::new)
            .stackTo(16)
            .shouldCustomRender(true)
            .tab(CreativeModeTab.TAB_FOOD)
            .submit(testRegistry);


    /*
    public static final FluidReg<ExampleFluid> exampleFluid = new FluidReg<ExampleFluid>("example_fluid")
            .still(ExampleFluid::new, "block/fluid/water_still")
            .flow(ExampleFluid::new, "block/fluid/water_flow")
            .overlayTexPath("block/fluid/water_overlay")
            .bucketItem(BucketItem::new)
            .blockType(ExampleFluidBlock::new)
            .submit(testRegistry);

     */

    public static final ChannelReg Channel = new ChannelReg("example_channel")
            .brand("1.0")
            .loadPacket(ExampleC2SPacket.class, ExampleC2SPacket::new)
            .loadPacket(ExampleS2CPacket.class, ExampleS2CPacket::new)
            .submit(testRegistry);

    public static void invoke(){
        testRegistry.submit();
    }
}
