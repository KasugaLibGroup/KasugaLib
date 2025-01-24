package kasuga.lib.core.client.frontend.rendering;

import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.client.render.texture.*;
import kasuga.lib.core.client.render.texture.old.SimpleTexture;
import kasuga.lib.core.client.render.texture.old.WorldTexture;
import kasuga.lib.core.util.LazyRecomputable;
import net.minecraft.resources.ResourceLocation;
import kasuga.lib.core.client.render.texture.*;
import org.joml.Vector3f;

public class BackgroundRenderer {

    public static enum RenderMode {
        COMMON,
        NINE_SLICED
    }

    public ResourceLocation location;

    public SimpleColor color = SimpleColor.fromRGBInt(0xffffff);
    public float opacity = 1.0f;
    public float borderSize = 0;
    public float borderScale = 1;
    private ImageProvider image;
    int left = 0;
    int top = 0;
    int width = 0;
    int height = 0;
    public LazyRecomputable<ImageMask> mask = LazyRecomputable.of(() -> {
        if (this.image == null) return null;
        StaticImage sim = this.image.getImage();
        if (sim == null) return null;
        ImageMask mask = sim.getMask();
        float w = sim.image.getWidth(), h = sim.image.getHeight();
        mask.setColor(color);
        return mask.rectangleUV(left / w, top / h, width == 0 ? 1f : (width + left) / w, height == 0 ? 1f : (height + top) / h);
    });

    public LazyRecomputable<NineSlicedImageMask> nineSlicedMask = LazyRecomputable.of(() ->{
        if (this.image == null) return null;
        StaticImage sim = this.image.getImage();
        if (sim == null) return null;
        NineSlicedImageMask mask = sim.getNineSlicedMask();
        float w = sim.image.getWidth(), h = sim.image.getHeight();
        mask.rectangleUV(left / w, top / h, width == 0 ? 1f : (width + left) / w, height == 0 ? 1f : (height + top) / h);
        mask.setBordersDirectly(borderSize / width, 1 - borderSize / width, borderSize / height, 1 - borderSize / height);
        mask.setScalingFactor(borderScale);
        mask.setColor(color);
        return mask;
    });

    RenderMode mode = RenderMode.COMMON;

    public void setRenderMode(RenderMode mode) {
        this.mode = mode;
    }

    public void render(RenderContext context,float x,float y,float width,float height){
        if( mode == RenderMode.COMMON ) {
            // this.renderCommon(context, x, y, width, height);
            neoRenderCommon(context, x, y, width, height);
        } else {
            // this.renderNineSliced(context, x, y, width, height);
            neoRenderNineSliced(context, x, y, width, height);
        }
    }

    public void setNineSlicedParam(float borderSize,float borderScale){
        this.borderSize = borderSize;
        this.borderScale = borderScale;
    }



    private void neoRenderNineSliced(RenderContext context, float x, float y, float width, float height) {
        if (mask == null) return;
        ImageMask imageMask = nineSlicedMask.get();
        imageMask.rectangle(new Vector3f(x, y, 0), ImageMask.Axis.X, ImageMask.Axis.Y, true, true, width, height);
        if (context.getContextType() == RenderContext.RenderContextType.SCREEN)
            imageMask.renderToGui(context.pose().last());
        else
            imageMask.renderToWorld(context.pose(), context.getBufferSource(),
                context.getRenderType().build(imageMask.image.id), false, context.packedLight);
    }

    public void neoRenderCommon(RenderContext context, float x, float y, float width, float height) {
        if (mask == null) return;
        ImageMask imageMask = mask.get();
        if (imageMask == null) return;
        imageMask.rectangle(new Vector3f(x, y, 0), ImageMask.Axis.X, ImageMask.Axis.Y, true, true, width, height);
        if (context.getContextType() == RenderContext.RenderContextType.SCREEN)
            imageMask.renderToGui(context.pose().last());
        else
            imageMask.renderToWorld(context.pose(), context.getBufferSource(),
                context.getRenderType().build(imageMask.image.id), false, context.packedLight);
    }

    public void markDirty(){
        this.mask.clear();
        this.nineSlicedMask.clear();
    }

    public void setImage(ImageProvider provider){
        this.image = provider;
        markDirty();
    }

    public void setUV(int left,int top,int width,int height){
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        markDirty();
    }

    public void setColor(SimpleColor color) {
        this.color = color;
        markDirty();
    }
}