package kasuga.lib.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.animation.data.Animation;
import kasuga.lib.core.client.gui.enums.PositionType;
import kasuga.lib.core.client.render.model.MultiPartModel;
import kasuga.lib.core.client.render.model.SimpleModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class Gui3DModel extends DataDrivenWidget {
    private SimpleModel model = null;
    private Animation animation = null;
    private int modelX, modelY, modelZ;

    public Gui3DModel(int x, int y, int width, int height, PositionType type, SimpleModel model) {
        this(x, y, width, height, type);
        this.model = model;
    }

    public Gui3DModel(int x, int y, int width, int height, PositionType type) {
        super(x, y, width, height, type);
    }

    public Gui3DModel(int width, int height, PositionType type, SimpleModel model) {
        this(width, height, type);
        this.model = model;
    }

    public Gui3DModel(int width, int height, PositionType type) {
        super(width, height, type);
    }

    public void setModel(SimpleModel model) {
        this.model = model;
    }

    public SimpleModel getModel() {
        return model;
    }

    public void setAnimation(Animation animation) {
        animation.loadModel(model);
    }

    public boolean hasAnimation() {
        return animation != null;
    }

    public Animation getAnimation() {
        return animation;
    }

    public boolean hasModel() {
        return model != null;
    }

    public boolean isMultiPartModel() {
        return hasModel() && model instanceof MultiPartModel;
    }

    public void setModelX(int x) {
        this.modelX = x;
    }

    public void setModelY(int y) {
        this.modelY = y;
    }

    public void setModelZ(int z) {
        this.modelZ = z;
    }

    public int modelX() {
        return modelX;
    }

    public int modelY() {
        return modelY;
    }

    public int modelZ() {
        return modelZ;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        if (animation != null) animation.action();
        MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        if (model != null) model.render(pPoseStack, buffer, modelX, modelY, modelZ, LightTexture.FULL_BLOCK, OverlayTexture.NO_OVERLAY);
    }
}
