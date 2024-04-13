package kasuga.lib.example_env.block.bogey.pk209p;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.bogey.BogeyRenderer;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.foundation.utility.NBTHelper;
import kasuga.lib.core.create.BogeyDataConstants;
import kasuga.lib.example_env.AllExampleBogey;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class PK209PRenderer extends BogeyRenderer {

    private static ResourceLocation asBlockModelResource(String path) {
        return AllExampleElements.testRegistry.asResource("block/" + path);
    }

    public static final PartialModel PK209P_MAIN = new PartialModel(asBlockModelResource("bogey/pk209p/pk209p_main"));
    public static final PartialModel PK209P_WHEEL = new PartialModel(asBlockModelResource("bogey/pk209p/pk209p_wheel"));
    public static final PartialModel PK209P_WHEEL2 = new PartialModel(asBlockModelResource("bogey/pk209p/pk209p_wheel2"));
    public static final PartialModel PK209P_MOTORWHEEL = new PartialModel(asBlockModelResource("bogey/pk209p/pk209_motorwheel"));
    @Override
    public void initialiseContraptionModelData(
            MaterialManager materialManager, CarriageBogey carriageBogey) {
        this.createModelInstance(
                materialManager, PK209P_MAIN, PK209P_WHEEL, PK209P_WHEEL2, PK209P_MOTORWHEEL);
    }

    @Override
    public BogeySizes.BogeySize getSize() {
        return AllExampleBogey.pk209p.getSize();
    }

    @Override
    public void render(CompoundTag bogeyData, float wheelAngle, PoseStack ms,
            int light, VertexConsumer vb, boolean inContraption) {

        Direction direction =
                bogeyData.contains(BogeyDataConstants.BOGEY_ASSEMBLY_DIRECTION_KEY)
                        ? NBTHelper.readEnum(
                        bogeyData,
                        BogeyDataConstants.BOGEY_ASSEMBLY_DIRECTION_KEY,
                        Direction.class)
                        : Direction.NORTH;

        boolean inInstancedContraption = vb == null;
        // 转向架架体
        BogeyModelData main = getTransform(PK209P_MAIN, ms, inInstancedContraption);
        // 发电轮对
        BogeyModelData wheel = getTransform(PK209P_WHEEL, ms, inInstancedContraption);
        // 普通轮对
        BogeyModelData wheel2 = getTransform(PK209P_WHEEL2, ms, inInstancedContraption);
        // 发电机小轮
        BogeyModelData motorWheel = getTransform(PK209P_MOTORWHEEL, ms, inInstancedContraption);

        if (direction == Direction.SOUTH || direction == Direction.EAST) {
            if (inContraption) {
                main.translate(0, 0.91, 0).render(ms, light, vb);
                wheel.translate(0, 0.8, 1.2).rotateX(wheelAngle).render(ms, light, vb);
                wheel2.translate(0, 0.8, -1.2).rotateX(wheelAngle).render(ms, light, vb);
                motorWheel
                        .translate(1.117, 0.82, 2.165)
                        .rotateX(wheelAngle * 3.256)
                        .render(ms, light, vb);
            } else {
                main.rotateY(180).translate(0, 0.91, 0).render(ms, light, vb);
                wheel.translate(0, 0.8, -1.2)
                        .rotateY(180)
                        .rotateX(-wheelAngle)
                        .render(ms, light, vb);
                wheel2.translate(0, 0.8, 1.2)
                        .rotateY(180)
                        .rotateX(-wheelAngle)
                        .render(ms, light, vb);
                motorWheel
                        .translate(-1.117, 0.82, -2.165)
                        .rotateY(180)
                        .rotateX(-wheelAngle * 3.256)
                        .render(ms, light, vb);
            }
        } else {
            main.translate(0, 0.91, 0).render(ms, light, vb);
            wheel.translate(0, 0.8, 1.2).rotateX(wheelAngle).render(ms, light, vb);
            wheel2.translate(0, 0.8, -1.2).rotateX(wheelAngle).render(ms, light, vb);
            motorWheel
                    .translate(1.117, 0.82, 2.165)
                    .rotateX(wheelAngle * 3.256)
                    .render(ms, light, vb);
        }
    }
}
