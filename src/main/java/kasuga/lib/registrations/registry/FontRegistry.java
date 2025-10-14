package kasuga.lib.registrations.registry;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.core.client.render.font.Font;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

/**
 * 这是用于注册自定义字体的注册类。你可以注册TTF字体（.ttf）或位图字体（json和bin）。
 * 你必须将你的字体放在"namespace:font"文件夹下。例如，如果你有一个
 * 名为example.ttf的字体文件在"namespace:font"文件夹下，那么请创建一个路径为
 * "namespace:example.ttf"的ResourceLocation。注意，你不应该在路径中输入"font/"，因为Minecraft
 * 的路径查找器会自动填充它。更多信息，请参见{@link Font}和{@link net.minecraft.network.chat.Style}。
 * This is the registry of custom fonts. You could register TTF fonts (.ttf) or bitmap fonts (jsons and bins).
 * You must prepare your font under namespace:font folder. For example, if you have a
 * font file called example.ttf under namespace:font folder, then please create a ResourceLocation with path
 * "namespace:example.ttf". Pay attention that you shouldn't type "font/" in your location path as the Minecraft
 * pathFinder would fill it automatically. For more info, please see {@link Font}
 * and {@link net.minecraft.network.chat.Style}.
 */
@Inner
public class FontRegistry {

    private final String namespace;
    @Inner
    private final HashMap<ResourceLocation, Font> listOfReg;

    @Inner
    public FontRegistry(String namespace) {
        this.namespace = namespace;
        this.listOfReg = new HashMap<>();
    }

    @Inner
    public void stackIn(Font reg) {
        listOfReg.put(reg.getLocation(), reg);
    }

    @Inner
    public void onRegister() {
        for(ResourceLocation location : listOfReg.keySet()) {
            listOfReg.get(location).loadFont();
        }
    }

    /**
     * 返回注册的字体
     * @param location 字体位置
     * @return 注册的字体。
     * returns the registered Font
     * @param location the font location
     * @return registered font.
     */
    public Font getFont(ResourceLocation location) {
        return listOfReg.getOrDefault(location, null);
    }
}
