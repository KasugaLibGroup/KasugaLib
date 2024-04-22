package kasuga.lib.core.client.gui.render;

import kasuga.lib.core.client.gui.context.RenderContext;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import kasuga.lib.core.client.render.texture.WorldTexture;
import kasuga.lib.core.util.LazyRecomputable;
import net.minecraft.resources.ResourceLocation;

public class BackgroundRender {
    public ResourceLocation location;
    public int color = 0xffffff;
    public float opacity = 1.0f;
    private ImageProvider image;
    int left = 0;
    int top = 0;
    int width = 0;
    int height = 0;
    public LazyRecomputable<SimpleTexture> simple = LazyRecomputable.of(()->{
        if(this.image == null)
            return null;
        return this.image.getSimpleTexture().cutSize(left,top,width,height).withColor(color,opacity);
    });

    public LazyRecomputable<WorldTexture> world = LazyRecomputable.of(()->{
        if(this.image == null)
            return null;
        return this.image.getWorldTexture().cutSize(left,top,width,height).withColor(color,opacity);
    });

    public void render(RenderContext context,int x,int y,int width,int height){
        if(context.getContextType() == RenderContext.RenderContextType.SCREEN){
            if(this.simple.get() == null)
                return;
            this.simple.get().render(x,y,width,height);
        }else{
            if(this.simple.get() == null)
                return;
            this.world.get().render(context.pose(),context.getBufferSource(),width,height,context.getLight());
        }
    }

    public void markDirty(){
        this.simple.clear();
        this.world.clear();
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
}
