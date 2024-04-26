package kasuga.lib.core.client.gui.context;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.util.Stack;

public class RenderContext {

    public static RenderContext fromScreen(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick){
        RenderContext context = new RenderContext(RenderContextType.SCREEN);
        context.setPoseStack(pPoseStack);
        context.setMouseContext(new PlaneMouseContext(pMouseX,pMouseY));
        context.setPartialTicks(pPartialTick);
        return context;
    }

    public static enum RenderContextType{
        WORLD, SCREEN
    }

    protected RenderContextType contextType;

    protected MultiBufferSource.BufferSource bufferSource;
    protected PoseStack poseStack;

    protected MouseContext mouse = MouseContext.EMPTY;

    protected float partialTicks = 0;

    protected int packedLight = LightTexture.FULL_BRIGHT;

    protected Stack<Integer> lightStack = new Stack<>();

    protected int packedOverlay = OverlayTexture.NO_OVERLAY;

    protected RenderType renderType = RenderType.solid();

    public PlaneVertexScissorStack scissorStack = new PlaneVertexScissorStack();

    public RenderContext(RenderContextType contextType){
        this.contextType = contextType;
    }

    public MouseContext mouse(){
        return mouse;
    }

    public PoseStack pose(){
        return poseStack;
    }

    public Matrix4f poseMatrix(){
        return pose().last().pose();
    }

    public void pushPose(PoseStack.Pose pose){
        this.pushPose(pose.pose());
    }

    public void pushPose(Matrix4f pose){
        this.pushPose();
        this.pose().mulPoseMatrix(pose);
    }

    public void pushPose(){
        this.poseStack.pushPose();
    }

    public void popPose(){
        this.poseStack.popPose();
    }

    public void pushLight(int light){
        this.lightStack.push(light);
        this.packedLight = light;
    }

    public void popLight(){
        this.lightStack.pop();
        this.packedLight = this.lightStack.isEmpty() ? 0 :this.lightStack.peek();
    }

    public MultiBufferSource.BufferSource getBufferSource(){
        return bufferSource;
    }

    protected int zStack;

    public void pushZStack(){
        zStack++;
        poseStack.translate(0,-0.01,0);
    }

    public void popZStack(){
        zStack--;
        poseStack.translate(0,0.01,0);
    }


    public void setPoseStack(PoseStack pPoseStack) {
        this.poseStack = pPoseStack;
    }

    public void setPartialTicks(float partialTicks){
        this.partialTicks = partialTicks;
    }

    public void setMouseContext(MouseContext mouse) {
        this.mouse = mouse;
    }

    public RenderContextType getContextType() {
        return contextType;
    }

    public int getLight() {
        return packedLight;
    }
}

