package kasuga.lib.core.client.gui.components;

import kasuga.lib.core.client.gui.context.RenderContext;
import kasuga.lib.core.client.gui.layout.yoga.YogaMeasureFunction;
import kasuga.lib.core.client.gui.layout.yoga.YogaMeasureOutput;
import kasuga.lib.core.client.gui.layout.yoga.YogaNodeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class Text extends DocumentNode{
    Font font = Minecraft.getInstance().font;
    String content = "";
    public Text(){
        super();
        this.locatorNode.setNodeType(YogaNodeType.TEXT);
    }

    public void setContent(String content) {
        this.content = content;
        markMeasureDirty();
    }

    @Override
    public YogaMeasureFunction measure() {
        return (node,width,withMode,height,heightMode)->{
            System.out.printf("Measure:" + content + "," + font.toString());
            return YogaMeasureOutput.make(font.width(this.content),font.lineHeight);
        };

    }

    @Override
    public void render(RenderContext context) {
        super.render(context);
        font.draw(context.pose(),content,positionCache.x,positionCache.y,0xff000000);
    }
}
