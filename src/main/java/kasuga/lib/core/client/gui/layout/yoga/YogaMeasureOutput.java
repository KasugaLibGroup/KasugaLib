package kasuga.lib.core.client.gui.layout.yoga;

public class YogaMeasureOutput {

    public static long make(float width, float height) {
        final int wBits = Float.floatToRawIntBits(width);
        final int hBits = Float.floatToRawIntBits(height);
        return ((long) wBits) << 32 | ((long) hBits);
    }

    public static long make(int width, int height) {
        return make((float) width, (float) height);
    }

    public static float getWidth(long measureOutput) {
        return Float.intBitsToFloat((int) (0xFFFFFFFF & (measureOutput >> 32)));
    }

    public static float getHeight(long measureOutput) {
        return Float.intBitsToFloat((int) (0xFFFFFFFF & measureOutput));
    }
}