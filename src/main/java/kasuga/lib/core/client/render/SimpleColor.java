package kasuga.lib.core.client.render;

import net.minecraft.nbt.CompoundTag;

import java.awt.*;
import java.awt.color.ColorSpace;

public class SimpleColor {
    Color color;

    protected SimpleColor(Color color) {
        this.color = color;
    }

    public SimpleColor copy() {
        return fromRGBA(this.getR(), this.getG(), this.getB(), this.getA());
    }

    public static SimpleColor fromRGBA(int r, int g, int b, float a) {
        return new SimpleColor(new Color(r, g, b, (int)(a * 256)));
    }

    public static SimpleColor fromRGBA(int r, int g, int b, int a) {
        return new SimpleColor(new Color(r, g, b, a));
    }

    public static SimpleColor fromRGBA(int rgb, float a) {
        return fromRGBAInt((rgb * 256 + (int)(a * 255f)));
    }

    public static SimpleColor fromRGBInt(int rgb) {
        return new SimpleColor(new Color(rgb));
    }

    public static SimpleColor fromRGBAInt(int rgba) {
        return new SimpleColor(new Color(rgba, rgba > 0xffffff));
    }

    public static SimpleColor fromHSVA(int h, int s, int v, float a) {
        return new SimpleColor(new Color(ColorSpace.getInstance(ColorSpace.TYPE_HSV),
                new float[]{((float) h)/360, ((float) s)/100, ((float) v)/100},
                a));
    }

    public static SimpleColor fromHSV(int h, int s, int v) {
        return fromHSVA(h, s, v, 1.0f);
    }

    public static SimpleColor fromHLSA(int h, int l, int s, float a) {
        return new SimpleColor(new Color(ColorSpace.getInstance(ColorSpace.TYPE_HLS),
                new float[]{((float) h)/360, ((float) l)/100, ((float) s)/100},
                a));
    }

    public SimpleColor setR(int red) {
        return SimpleColor.fromRGBA(red, this.getG(), this.getB(), this.getA());
    }

    public SimpleColor setG(int green) {
        return SimpleColor.fromRGBA(this.getR(), green, this.getB(), this.getA());
    }

    public SimpleColor setB(int blue) {
        return SimpleColor.fromRGBA(getR(), getG(), blue, this.getA());
    }

    public SimpleColor setA(float alpha) {
        return SimpleColor.fromRGBA(getR(), getG(), getB(), alpha);
    }

    public static SimpleColor fromHLS(int h, int l, int s) {
        return fromHLSA(h, l, s, 1.0f);
    }
    
    public static SimpleColor fromNbt(CompoundTag tag) {
        return fromRGBA(tag.getInt("red"), tag.getInt("green"), tag.getInt("blue"), tag.getFloat("alpha"));
    }
    
    public void toNbt(CompoundTag tag) {
        tag.putInt("red", getR());
        tag.putInt("green", getG());
        tag.putInt("blue", getB());
        tag.putFloat("alpha", getA());
    }

    public int getR() {
        return color.getRed();
    }

    public float getfR() {
        return ((float) getR()) / 255;
    }

    public int getG() {
        return color.getGreen();
    }

    public float getfG() {
        return ((float) getG()) / 255;
    }

    public int getB() {
        return color.getBlue();
    }

    public float getfB() {
        return ((float) getB()) / 255;
    }

    public float getA() {
        return ((float) color.getAlpha() / 256);
    }

    public Color getColor() {
        return color;
    }

    public int getRGB() {
        return color.getRGB();
    }

    public int getRGBA() {
        return color.getRGB() * 256 + color.getAlpha();
    }

    public int getHsv() {
        return (int)(color.getComponents(ColorSpace.getInstance(ColorSpace.TYPE_HSV), new float[0])[0] * 360);
    }

    public int gethSv() {
        return (int)(color.getComponents(ColorSpace.getInstance(ColorSpace.TYPE_HSV), new float[0])[1] * 100);
    }

    public int gethsV() {
        return (int)(color.getComponents(ColorSpace.getInstance(ColorSpace.TYPE_HSV), new float[0])[2] * 100);
    }

    public int getHls() {
        return (int)(color.getComponents(ColorSpace.getInstance(ColorSpace.TYPE_HLS), new float[0])[0] * 360);
    }

    public int gethLs() {
        return (int)(color.getComponents(ColorSpace.getInstance(ColorSpace.TYPE_HLS), new float[0])[1] * 100);
    }

    public int gethlS() {
        return (int)(color.getComponents(ColorSpace.getInstance(ColorSpace.TYPE_HLS), new float[0])[2] * 100);
    }
}
