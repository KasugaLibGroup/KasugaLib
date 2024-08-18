package kasuga.lib.core.client.frontend.font;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.world.phys.Vec2;

public class FontHelper {

    public static void draw(Font font, PoseStack pose, Vec2 originalPoint, int fontHeight, String text, int color){
        draw(font, pose, originalPoint, fontHeight, text, color, 1.0f);
    }

    public static void draw(Font font, PoseStack pose, Vec2 originalPoint, int fontHeight, String text, int color, float fontWidthScale){
        float yScale = (float) fontHeight / (float) font.lineHeight;
        float xScale = fontWidthScale * yScale;
        pose.pushPose();
        pose.translate(originalPoint.x,originalPoint.y,0);
        pose.scale(xScale,yScale,0);
        font.draw(pose, text, 0,0, color);
        pose.popPose();
    }

    public static Vec2 measure(Font font, int fontHeight, String text, float fontWidthScale){
        float yScale = (float) fontHeight / (float) font.lineHeight;
        float xScale = fontWidthScale * yScale;
        return new Vec2(
                xScale * font.width(text),
                yScale * font.lineHeight
        );
    }
}
