package kasuga.lib.example_env;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.base.BucketItem;
import kasuga.lib.core.base.commands.CommandHandler;
import kasuga.lib.core.client.interaction.GuiOperatingPerspectiveScreen;
import kasuga.lib.core.menu.base.GuiBinding;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.base.GuiMenuRegistry;
import kasuga.lib.core.menu.base.GuiMenuType;
import kasuga.lib.core.util.Envs;
import kasuga.lib.example_env.block.RotationTestBlock;
import kasuga.lib.example_env.block.fluid.ExampleFluid;
import kasuga.lib.example_env.block.fluid.ExampleFluidBlock;
import kasuga.lib.example_env.block.green_apple.GreenAppleBlock;
import kasuga.lib.example_env.block.green_apple.GreenAppleItem;
import kasuga.lib.example_env.block.green_apple.GreenAppleTile;
import kasuga.lib.example_env.block.gui.GuiExampleBlock;
import kasuga.lib.example_env.block.gui.GuiExampleBlockEntity;
import kasuga.lib.example_env.block.gui.GuiExampleBlockRenderer;
import kasuga.lib.example_env.block.gui.GuiExampleMenu;
import kasuga.lib.example_env.client.block_entity.renderer.GreenAppleTileRenderer;
import kasuga.lib.example_env.client.screens.GreenAppleMenu;
import kasuga.lib.example_env.client.screens.GreenAppleScreen;
import kasuga.lib.example_env.network.ExampleC2SPacket;
import kasuga.lib.example_env.network.ExampleS2CPacket;
import kasuga.lib.registrations.common.*;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fml.DistExecutor;

public class AllExampleElements {

    public static final SimpleRegistry REGISTRY = new SimpleRegistry(KasugaLib.MOD_ID,KasugaLib.EVENTS);

    public static final BlockReg<GreenAppleBlock> greenApple =
            new BlockReg<GreenAppleBlock>("green_apple")
            .blockType(GreenAppleBlock::new)
            .material(Material.AIR)
            .materialColor(MaterialColor.COLOR_GREEN)
            .withSound(SoundType.CROP)
            .defaultBlockItem(new ResourceLocation(KasugaLib.MOD_ID, "block/test/green_apple"))
            .stackSize(32)
            .tabTo(CreativeModeTab.TAB_DECORATIONS)
            .submit(REGISTRY);

    public static final BlockEntityReg<GreenAppleTile> greenAppleTile =
            new BlockEntityReg<GreenAppleTile>("green_apple_tile")
            .blockEntityType(GreenAppleTile::new)
            .withRenderer(() -> GreenAppleTileRenderer::new)
            .blockPredicates((location, block) -> block instanceof GreenAppleBlock)
            .submit(REGISTRY);

    public static final BlockReg<GuiExampleBlock> guiExampleBlock =
            new BlockReg<GuiExampleBlock>("gui_example_block")
                    .blockType(GuiExampleBlock::new)
                    .material(Material.AIR)
                    .defaultBlockItem()
                    .tabTo(CreativeModeTab.TAB_DECORATIONS)
                    .submit(REGISTRY);

    public static final BlockEntityReg<GuiExampleBlockEntity> guiExampleTile =
            new BlockEntityReg<GuiExampleBlockEntity>("gui_example_tile")
                    .blockEntityType(GuiExampleBlockEntity::new)
                    .blockPredicates((location, block) -> block instanceof GuiExampleBlock)
                    .withRenderer(()-> GuiExampleBlockRenderer::new)
                    .submit(REGISTRY);


    /*
    public static final EntityReg<WuLingEntity> wuling = new EntityReg<WuLingEntity>("wuling")
            .entityType(WuLingEntity::new)
            .size(3, 3)
            .attribute(WuLingEntity::createAttributes)
            .withRenderer(() -> (WuLingRenderer::new))
            .submit(REGISTRY);
     */

    /*
    public static final ModelReg greenAppleModel = new ModelReg("green_apple", new ResourceLocation(KasugaLib.MOD_ID, "block/test/green_apple"))
            .submit(REGISTRY);

     */

    /*
    public static final ModelReg wuLingVans = new
            ModelReg("wuling_vans", new ResourceLocation(KasugaLib.MOD_ID, "entity/test/wuling/wuling_base"))
            .submit(REGISTRY);

     */

    public static final ItemReg<GreenAppleItem> greenAppleItem =
            new ItemReg<GreenAppleItem>("green_apple_item")
            .itemType(GreenAppleItem::new)
            .stackTo(16)
            .shouldCustomRender(true)
            // .tab(tab)
            .submit(REGISTRY);

    public static final CreativeTabReg tab = new CreativeTabReg("test")
            .icon(greenAppleItem).submit(REGISTRY);

    public static final BlockReg<RotationTestBlock> rotationTest =
            new BlockReg<RotationTestBlock>("rotation_test")
                    .blockType(RotationTestBlock::new)
                    .defaultBlockItem()
                    .tabTo(tab)
                    .addProperty(BlockBehaviour.Properties::noCollission)
                    .submit(REGISTRY);
    /*
    public static final AnimReg test_anim =
            new AnimReg("test_anim", REGISTRY.asResource("models/entity/test/wuling/wuling_anim.json"))
            .submit(REGISTRY);

     */

    /*
    public static final SimpleConfig config = new SimpleConfig()
            .common("common settings")
            .rangedIntConfig("cfg1", "this is a test cfg", 0, -1, 1)
            .doubleConfig("cfg2", 0d)
            .client("only in client")
            .boolConfig("bool", false)
            .server("only in server")
            .intConfig("int_cfg", 4)
            .registerConfigs();
     */



    public static final FluidReg<ExampleFluid> exampleFluid = new FluidReg<ExampleFluid>("example_fluid")
            .still(ExampleFluid::new, "block/fluid/water_still")
            .flow(ExampleFluid.Flowing::new, "block/fluid/water_flow")
            .numericProperties(1, 8, 3, 10)
            .overlayTexPath("block/fluid/water_overlay")
            .bucketItem(BucketItem::new)
            .basicFluidProperties(5, 15, 5, true)
            .defaultSounds()
            .tintColor(0xFFD2691E)
            .fogColor(210, 105, 30)
            .blockType((fluid, properties) ->
                    new ExampleFluidBlock(fluid, BlockBehaviour.Properties.copy(Blocks.WATER)))
            .noLootAndOcclusion()
            .setRenderType("translucent")
            .tab(tab)
            .submit(REGISTRY);

    public static final BlockReg<DropExperienceBlock> SALT_ORE =
            new BlockReg<DropExperienceBlock>("salt_ore")
                    .blockType(props ->
                            new DropExperienceBlock(props, UniformInt.of(3, 7)))
                    .material(Material.STONE)
                    .materialColor(MaterialColor.STONE)
                    .addProperty(properties -> properties.strength(1.5f, 6.0F))
                    .addProperty(BlockBehaviour.Properties::requiresCorrectToolForDrops)
                    .defaultBlockItem()
                    .tabTo(tab)
                    .submit(AllExampleElements.REGISTRY);

    public static final OreReg<DropExperienceBlock> exampleOreConfig = new OreReg<DropExperienceBlock>("salt_ore")
            .setOreBlock(SALT_ORE::getBlock)
            .addOreReplaceTarget()
            .addDeepSlateReplaceTarget()
            .setOreCountPerChunk(20)
            .setOreQuantityPerGroup(64)
            .setOreDistributionType(PlacedFeatureReg.DistributionType.TRIANGLE)
            .setOreAnchorAbsolute(80, -80)
            .submit(AllExampleElements.REGISTRY);

    public static final MenuReg<GreenAppleMenu, GreenAppleScreen> apple =
            new MenuReg<GreenAppleMenu, GreenAppleScreen>("green_apple_screen")
                    .withMenuAndScreen(GreenAppleMenu::new, () -> GreenAppleScreen::new)
                    .submit(REGISTRY);


    public static final ChannelReg Channel = new ChannelReg("example_channel")
            .brand("1.0")
            .loadPacket(ExampleC2SPacket.class, ExampleC2SPacket::new)
            .loadPacket(ExampleS2CPacket.class, ExampleS2CPacket::new)
            .submit(REGISTRY);


    public static final GuiMenuType<GuiExampleMenu> MENU_EXAMPLE = GuiMenuType.createType(GuiExampleMenu::new);


    /*
    public static final KeyBindingReg key = new KeyBindingReg("oo", "saas")
            .setKeycode(GLFW.GLFW_KEY_0, InputConstants.Type.KEYSYM)
            .setModifier(KeyModifier.CONTROL)
            .setEnvironment(KeyBindingReg.Environment.IN_GUI)
            .setClientHandler(System.out::println)
            .setServerHandler(System.out::println)
            .submit(REGISTRY);
     */

    public static final CommandReg OPERATE_COMMAND = new CommandReg("operate")
            .onlyIn(Dist.CLIENT)
            .setHandler(new CommandHandler() {
                @Override
                public void run() {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->OperateScreenCommand::invoke);
                }
            })
            .submit(REGISTRY);

    public static void invoke() {
        if (Envs.isClient()) AllClient.invoke();
        REGISTRY.submit();
    }
}
