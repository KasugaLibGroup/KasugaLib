package kasuga.lib.example_env;

import com.simibubi.create.content.trains.bogey.BogeyBlockEntityRenderer;
import kasuga.lib.example_env.block.bogey.cw2.CW2BogeyRenderer;
import kasuga.lib.example_env.block.bogey.cw2.TestBogeyBlock;
import kasuga.lib.example_env.block.bogey.cw2.TestBogeyBlockEntity;
import kasuga.lib.example_env.block.bogey.pk209p.PK209PRenderer;
import kasuga.lib.example_env.block.bogey.pk209p.StandardBogeyBlock;
import kasuga.lib.example_env.block.bogey.pk209p.StandardBogeyBlockEntity;
import kasuga.lib.registrations.common.BlockEntityReg;
import kasuga.lib.registrations.create.BogeyBlockReg;
import kasuga.lib.registrations.create.BogeyGroupReg;
import kasuga.lib.registrations.create.BogeySizeReg;
import kasuga.lib.registrations.registry.CreateRegistry;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class AllExampleBogey {

    public static final CreateRegistry testRegistry = ExampleMain.testRegistry;

    public static final BogeySizeReg pk209p = new BogeySizeReg("pk209p")
            .size(0.915F / 2F)
            .submit(testRegistry);

    public static final BogeySizeReg cw2 = new BogeySizeReg("cw2")
            .size(0.915F / 2F)
            .submit(testRegistry);

    public static final BogeyGroupReg standardGroup = new BogeyGroupReg("standard", "test_bogey")
            .bogey(AllExampleBogey.pk209p.getSize(), PK209PRenderer::new, testRegistry.asResource("pk209p_bogey"))
            .translationKey("standard_group")
            .submit(testRegistry);

    public static final BogeyGroupReg testGroup = new BogeyGroupReg("test", "test_bogey")
            .bogey(AllExampleBogey.cw2.getSize(), CW2BogeyRenderer::new, testRegistry.asResource("cw2_bogey"))
            .translationKey("test_group")
            .submit(testRegistry);
    public static final BogeyBlockReg<StandardBogeyBlock> standardBogey =
            new BogeyBlockReg<StandardBogeyBlock>("pk209p_bogey")
            .block(StandardBogeyBlock::new)
            .material(Material.METAL)
            .materialColor(MaterialColor.PODZOL)
            .size(pk209p)
            .translationKey("standard_bogey")
            .submit(testRegistry);

    public static final BogeyBlockReg<TestBogeyBlock> cw2Bogey =
            new BogeyBlockReg<TestBogeyBlock>("cw2_bogey")
                    .block(TestBogeyBlock::new)
                    .material(Material.METAL)
                    .materialColor(MaterialColor.PODZOL)
                    .size(cw2)
                    .translationKey("cw2_bogey")
                    .submit(testRegistry);

    public static final BlockEntityReg<StandardBogeyBlockEntity> standardBogeyEntity =
            new BlockEntityReg<StandardBogeyBlockEntity>("standard_bogey_entity")
            .blockEntityType(StandardBogeyBlockEntity::new)
            .addBlock(() -> standardBogey.getEntry().get())
            .withRenderer(BogeyBlockEntityRenderer::new)
            .submit(testRegistry);

    public static final BlockEntityReg<TestBogeyBlockEntity> testBogeyEntity =
            new BlockEntityReg<TestBogeyBlockEntity>("test_bogey_entity")
                    .blockEntityType(TestBogeyBlockEntity::new)
                    .addBlock(() -> cw2Bogey.getEntry().get())
                    .withRenderer(BogeyBlockEntityRenderer::new)
                    .submit(testRegistry);

    public static void invoke(){}

}
