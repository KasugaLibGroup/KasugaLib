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
        return new SimpleColor(new Color(r, g, b, (int)(a * 255)));
    }

    public static SimpleColor fromRGBA(int r, int g, int b, int a) {
        return new SimpleColor(new Color(r, g, b, a));
    }

    public static SimpleColor fromRGBA(int rgb, float a) {
        return fromRGBAInt((int)(a * 255) * 256 * 256 * 256 + rgb);
    }

    public static SimpleColor fromHexString(String hex){
        int[] color = hexToRgba(hex);
        return fromRGBA(color[0], color[1], color[2], color[3]);
    }

    public static SimpleColor fromRGB(float r, float g, float b) {
        return fromRGBA((int)(r * 255), (int)(g * 255), (int)(b * 255), 1);
    }

    public static SimpleColor fromRGBInt(int rgb) {
        return new SimpleColor(new Color(rgb));
    }

    public static SimpleColor fromRGBAInt(int rgba) {
        return new SimpleColor(new Color(rgba, rgba > 0xffffff));
    }

    public static SimpleColor fromHSV(float h, float s, float v) {
        return fromHSVA(h, s, v, 1);
    }

    public static SimpleColor fromHSVA(float h, float s, float v, float a) {
        float[] rgb = hsvToRgb(h, s, v);
        return new SimpleColor(new Color((int) rgb[0], (int) rgb[1], (int) rgb[2], (int)(a * 255)));
    }

    public static SimpleColor fromHSI(float h, float s, float i) {
        return fromHSIA(h, s, i, 1);
    }

    public static SimpleColor fromHSIA(float h, float s, float i, float a) {
        float[] rgb = hsiToRgb(h, s, i);
        return new SimpleColor(new Color((int) rgb[0], (int) rgb[1], (int) rgb[2], (int)(a * 255)));
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

    public float[] getHSI() {
        return rgbToHsi(this.getR(), this.getG(), this.getB());
    }

    public float[] getHSV() {
        return rgbToHsv(this.getR(), this.getG(), this.getB());
    }

    /**
     * convert color space from HSI to RGB
     * @param h hue, [0, 360)
     * @param s Saturation , [0, 1]
     * @param i [0, 255]
     * @return rgb values, all ranged [0, 255]
     */
    public static float[] hsiToRgb(float h, float s, float i) {
        while (h < 0) h += 360;
        h %= 360;
        float r, g, b;
        if (h >= 0 && h < 120) {
            b = i * (1 - s);
            r = i * (float) (1 + (s * Math.cos(h)) / Math.cos(Math.PI / 3 - h));
            g = 3 * i - (r + b);
        } else if (h >= 120 && h < 240) {
            h -= 120f;
            r = i * (1 - s);
            g = i * (float) (1 + (s * Math.cos(h)) / Math.cos(Math.PI / 3 - h));
            b = 3 * i - (r + g);
        } else {
            h -= 240f;
            g = i * (1 - s);
            b = i * (float) (1 + (s * Math.cos(h)) / Math.cos(Math.PI / 3 - h));
            r = 3 * i - (g + b);
        }
        return new float[]{r, g, b};
    }

    /**
     * convert color space HSV to RGB
     * @param h Hue, [0, 360)
     * @param s Saturation, [0, 1]
     * @param v Value, [0, 1]
     * @return rgb values, all ranged [0, 255]
     */
    public static float[] hsvToRgb(float h, float s, float v) {
        while (h < 0) h += 360;
        h %= 360;
        float c = v * s;
        float x = c * (1 - Math.abs((h / 60f) % 2 - 1));
        float m = v - c;
        float r1 = 0, g1 = 0, b1 = 0;
        if (h >= 0 && h < 60) {
            r1 = c;
            g1 = x;
            b1 = 0;
        } else if (h >= 60 && h < 120) {
            r1 = x;
            g1 = c;
            b1 = 0;
        } else if (h >= 120 && h < 180) {
            r1 = 0;
            g1 = c;
            b1 = x;
        } else if (h >= 180 && h < 240) {
            r1 = 0;
            g1 = x;
            b1 = c;
        } else if (h >= 240 && h < 300) {
            r1 = x;
            g1 = 0;
            b1 = c;
        } else if (h >= 300 && h < 360) {
            r1 = c;
            g1 = 0;
            b1 = x;
        }
        return new float[]{(r1 + m) * 255, (g1 + m) * 255, (b1 + m) * 255};
    }

    /**
     * convert color space RGB to HSV.
     * @param r red, [0, 255]
     * @param g green, [0, 255]
     * @param b blue, [0, 255]
     * @return h, [0, 360); s, [0, 1]; v, [0, 1]
     */
    public static float[] rgbToHsv(float r, float g, float b) {
        r /= 255;
        g /= 255;
        b /= 255;
        float h = 0, s = 0, v = 0;
        float cMax = Math.max(Math.max(r, g), b);
        float cMin = Math.min(Math.min(r, g), b);
        float delta = cMax - cMin;

        if (delta == 0) h = 0;
        else if (cMax == r) h = 60 * (((g - b) / delta) % 6);
        else if (cMax == g) h = 60 * ((b - r) / delta + 2);
        else h = 60 * ((r - g) / delta + 4);

        if (cMax == 0) s = 0;
        else s = delta / cMax;

        v = cMax;
        return new float[]{h, s, v};
    }

    /**
     * convert color space RGB to HSI
     * @param r red, [0, 255]
     * @param g green, [0, 255]
     * @param b blue, [0, 255]
     * @return h, [0, 360); s, [0, 1]; i, [0, 255]
     */
    public static float[] rgbToHsi(float r, float g, float b) {
        float theta = (float) Math.acos((.5 * (2 * r - g - b)) /
                Math.sqrt(Math.pow(r - g, 2) + (r - b) * (g - b)));
        float i = (r + g + b) / 3;
        float s = 1 - Math.min(r, Math.min(g, b)) / i;
        float h = b <= g ? theta : 360 - theta;
        return new float[]{h, s, i};
    }

    public static int[] hexToRgba(String hexString){
        try {
            switch (hexString.length()){
                case 3:
                    return new int[]{
                            Integer.parseInt(hexString.substring(0,1)),
                            Integer.parseInt(hexString.substring(1,2)),
                            Integer.parseInt(hexString.substring(2,3)),
                            1
                    };
                case 4:
                    return new int[]{
                            Integer.parseInt(hexString.substring(0,1),16),
                            Integer.parseInt(hexString.substring(1,2),16),
                            Integer.parseInt(hexString.substring(2,3),16),
                            Integer.parseInt(hexString.substring(3,4),16)
                    };
                case 6:
                    return new int[]{
                            Integer.parseInt(hexString.substring(0,2),16),
                            Integer.parseInt(hexString.substring(2,4),16),
                            Integer.parseInt(hexString.substring(4,6),16),
                            1
                    };
                case 8:
                    return new int[]{
                            Integer.parseInt(hexString.substring(0,2),16),
                            Integer.parseInt(hexString.substring(2,4),16),
                            Integer.parseInt(hexString.substring(4,6),16),
                            Integer.parseInt(hexString.substring(6,8),16)
                    };
            }
        }catch (NumberFormatException e){}
        return new int[]{0, 0, 0, 0};
    }
}
