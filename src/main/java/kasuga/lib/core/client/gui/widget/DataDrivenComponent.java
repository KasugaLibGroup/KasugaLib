package kasuga.lib.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.enums.ComponentType;
import kasuga.lib.core.client.gui.enums.LocationType;
import kasuga.lib.core.client.render.component.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;

public class DataDrivenComponent extends DataDrivenWidget {
    ComponentType type = ComponentType.EMPTY;
    private final net.minecraft.client.gui.Font f = Minecraft.getInstance().font;
    Font font = new Font();
    MutableComponent component = null;
    public String content = "";

    public DataDrivenComponent(int x, int y, int width, int height, LocationType location, ComponentType componentType) {
        this(x, y, width, height, location);
        this.type = componentType;
    }

    public DataDrivenComponent(int x, int y, int width, int height, LocationType location) {
        super(x, y, width, height, location);
    }

    public DataDrivenComponent(int width, int height, LocationType location, ComponentType componentType) {
        this(width, height, location);
        this.type = componentType;
    }

    public DataDrivenComponent(int width, int height, LocationType location) {
        super(width, height, location);
    }

    @Override
    public void init() {
        component = type.getComponent(content).withStyle(font.getFont());
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        f.draw(pPoseStack, component, this.x, this.y, font.getFont().getColor().getValue());
    }
}
