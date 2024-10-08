package kasuga.lib.core.client.render.font;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.client.render.texture.Vec2f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.util.function.Consumer;

public class TextContext {
    private final Font font;
    private Style style;
    private final Component text;
    private SimpleColor color;
    private Vec2f scale, pivot;
    private Vector3f rotation;
    public static final Vec2f ONE = new Vec2f(1, 1);

    public TextContext(Font font, Component component) {
        this.font = font;
        this.text = component;
        scale = ONE;
        pivot = Vec2f.ZERO;
        rotation = Vector3f.ZERO;
        style = null;
    }

    public TextContext(Component component) {
        this(null, component);
    }

    public TextContext(Font font, String text) {
        this(font, Component.literal(text));
    }

    public TextContext(String text) {
        this(null, text);
    }

    public Vec2f getPivot() {
        return pivot;
    }

    public void setPivot(Vec2f pivot) {
        this.pivot = pivot;
    }

    public void setPivot(float px, float py) {
        this.pivot = new Vec2f(px, py);
    }

    public void setScale(Vec2f scale) {
        this.scale = scale;
    }

    public void setScale(float scaleX, float scaleY) {
        scale.setX(scaleX);
        scale.setY(scaleY);
    }

    public Vec2f getScale() {
        return scale;
    }

    public float getScaleX() {
        return scale.x();
    }

    public float getScaleY() {
        return scale.y();
    }

    public float getWidth() {
        this.getFont().applyTo(text.getStyle());
        return Minecraft.getInstance().font.width(text) * this.scale.x();
    }

    public float getHeight() {
        return Minecraft.getInstance().font.lineHeight * this.scale.y();
    }

    public void setColor(SimpleColor color) {
        this.color = color;
    }

    public SimpleColor getColor() {
        return color;
    }

    public Component getText() {
        return text;
    }

    public Style getFont() {
        if (style != null) return style;
        if (font == null) {
            style = Style.EMPTY;
            return style;
        } else {
            style = font.getFont();
            return style;
        }
    }

    public void resetFont() {
        style = null;
        getFont();
    }

    public void rotate(Vector3f rotation) {
        Quaternion quaternion = Quaternion.fromXYZ(this.rotation);
        Quaternion other = Quaternion.fromXYZ(rotation);
        quaternion.mul(other);
        this.rotation = quaternion.toXYZ();
    }

    public void rotateDeg(Vector3f rotationDeg) {
        rotationDeg = rotationDeg.copy();
        rotationDeg.mul((float) Math.PI / 180f);
        rotate(rotationDeg);
    }

    public void rotate(float x, float y, float z) {
        this.rotate(new Vector3f(x, y, z));
    }

    public void rotateDeg(float x, float y, float z) {
        this.rotateDeg(new Vector3f(x, y, z));
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation = new Vector3f(x, y, z);
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setInsertion(String insertion) {
        getFont().withInsertion(insertion);
    }

    public void setClickEvent(ClickEvent clickEvent) {
        getFont().withClickEvent(clickEvent);
    }

    public void setHoverEvent(HoverEvent event) {
        getFont().withHoverEvent(event);
    }

    public void applyFormat(ChatFormatting... formats) {
        getFont().applyFormats(formats);
    }

    public boolean isUnderlined() {
        return getFont().isUnderlined();
    }

    public void underline(boolean underline) {
        this.getFont().withUnderlined(underline);
    }

    public void bold(boolean bold) {
        getFont().withBold(bold);
    }

    public boolean isBold() {
        return getFont().isBold();
    }

    public void strikeThrough(boolean strikeThrough) {
        getFont().withStrikethrough(strikeThrough);
    }

    public boolean isStrikeThrough() {
        return getFont().isStrikethrough();
    }

    public void obfuscated(boolean obfuscated) {
        getFont().withObfuscated(obfuscated);
    }

    public boolean isObfuscated() {
        return getFont().isObfuscated();
    }

    public void italic(boolean italic) {
        getFont().withItalic(italic);
    }

    public void renderToGui(PoseStack pose) {
        transform(pose, obj -> {
            Minecraft.getInstance().font.draw(pose, text, 0, 0, color.getRGB());
        });
    }

    public void renderToWorld(PoseStack pose, MultiBufferSource source, boolean dropShadow, boolean transparent, SimpleColor bgColor, int light) {
        transform(pose, obj -> {
            Minecraft.getInstance().font.drawInBatch(text, 0 ,0, color.getRGB(), dropShadow, pose.last().pose(),
                    source, transparent, bgColor.getRGB(), light);
        });
    }

    public void renderToWorld(PoseStack pose, MultiBufferSource source, boolean dropShadow, int light) {
        renderToWorld(pose, source, dropShadow, true, SimpleColor.fromRGBA(0, 0, 0, 0), light);
    }

    public void renderToWorld(PoseStack pose, MultiBufferSource source, int light) {
        renderToWorld(pose, source, false, light);
    }

    private void transform(PoseStack pose, Consumer<Object> func) {
        pose.mulPose(Quaternion.fromXYZ(this.rotation));
        pose.scale(this.scale.x(), this.scale.y(), 1f);
        pose.translate(- pivot.x() * this.getWidth(), - pivot.y() * this.getHeight(), 0);
        func.accept(null);
        pose.translate(pivot.x() * this.getWidth(), pivot.y() * this.getHeight(), 0);
        pose.scale(1 / this.scale.x(), 1 / this.scale.y(), 1f);
        Vector3f negRot = rotation.copy();
        negRot.mul(-1f);
        pose.mulPose(Quaternion.fromXYZ(negRot));
    }
}
