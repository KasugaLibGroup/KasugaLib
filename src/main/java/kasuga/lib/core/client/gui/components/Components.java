package kasuga.lib.core.client.gui.components;

public class Components {
    public static ComponentType<View> VIEW =
            ComponentRegistry.registerNative("view", SimpleComponentType.of(View::new));

    public static ComponentType<Text> TEXT =
            ComponentRegistry.registerNative("text", SimpleComponentType.of(Text::new));

    public static void init() {

    }
}
