package kasuga.lib.core.client.frontend.gui.layout.yoga.api;

public interface YogaMeasureFunction {
    /**
     * Return a value created by YogaMeasureOutput.make(width, height);
     */
    long measure(
            YogaNode node,
            float width,
            YogaMeasureMode widthMode,
            float height,
            YogaMeasureMode heightMode);
}
