package kasuga.lib.core.client.frontend.gui.nodes;

import kasuga.lib.core.client.frontend.common.layouting.LayoutBox;
import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.dom.attribute.AttributeProxy;
import kasuga.lib.core.client.frontend.gui.GuiContext;
import kasuga.lib.core.client.frontend.gui.layout.yoga.MayMeasurable;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaMeasureFunction;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaMeasureMode;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaMeasureOutput;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaNode;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.util.Optional;

public class GuiTextNode extends GuiDomNode implements MayMeasurable {

    Font font = Minecraft.getInstance().font;

    String content = "";

    LazyRecomputable<Pair<Integer,Integer>> measureResult = LazyRecomputable.of(()->{
        return Pair.of(font.width(content),font.lineHeight);
    });

    GuiTextNode(GuiContext context) {
        super(context);
        this.attributes.registerProxy("content", new AttributeProxy() {
            @Override
            public String get() {
                return content;
            }

            @Override
            public String set(String value) {
                content = value;
                getLayoutManager().markDirty();
                return value;
            }
        });
    }

    @Override
    public void render(Object source, RenderContext context) {
        super.render(source, context);
        LayoutNode layout = getLayoutManager().getSourceNode(source);
        LayoutBox box = layout.getPosition();
        font.draw(context.pose(),content,box.x,box.y,0xff000000);
    }

    @Override
    public Optional<YogaMeasureFunction> measure() {
        return Optional.of((YogaNode node,
                            float width,
                            YogaMeasureMode widthMode,
                            float height,
                            YogaMeasureMode heightMode)->{
            Pair<Integer, Integer> result = measureResult.get();
            int measuredWidth = result.getFirst();
            int measuredHeight = result.getSecond();

            float finalWidth = (widthMode == YogaMeasureMode.EXACTLY) ? width :
                    (widthMode == YogaMeasureMode.AT_MOST) ? Math.min(width, measuredWidth) :
                            measuredWidth;

            float finalHeight = (heightMode == YogaMeasureMode.EXACTLY) ? height :
                    (heightMode == YogaMeasureMode.AT_MOST) ? Math.min(height, measuredHeight) :
                            measuredHeight;

            return YogaMeasureOutput.make(finalWidth, finalHeight);
        });
    }
}
