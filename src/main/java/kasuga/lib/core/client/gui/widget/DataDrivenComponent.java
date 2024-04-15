package kasuga.lib.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.enums.ComponentType;
import kasuga.lib.core.client.gui.enums.DisplayType;
import kasuga.lib.core.client.gui.enums.PositionType;
import kasuga.lib.core.client.render.component.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;

public class DataDrivenComponent extends DataDrivenWidget {
    ComponentType type = ComponentType.EMPTY;
    private final net.minecraft.client.gui.Font f = Minecraft.getInstance().font;
    Font font = new Font();
    MutableComponent component = null;
    public String content = "";

    public DataDrivenComponent(int x, int y, int width, int height, PositionType location, ComponentType componentType, DisplayType displayType) {
        this(x, y, width, height, location, displayType);
        this.type = componentType;
    }

    public DataDrivenComponent(int x, int y, int width, int height, PositionType location, DisplayType displayType) {
        super(x, y, width, height, location,displayType);
    }

    public DataDrivenComponent(int width, int height, PositionType location, ComponentType componentType, DisplayType displayType) {
        this(width, height, location,displayType);
        this.type = componentType;
    }

    public DataDrivenComponent(int width, int height, PositionType location, DisplayType displayType) {
        super(width, height, location, displayType);
    }

    @Override
    public void init() {
        component = type.getComponent(content).withStyle(font.getFont());
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.updatePosition();
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        f.draw(pPoseStack, component, this.x, this.y, font.getFont().getColor().getValue());
    }

    public void setComponentType(ComponentType type) {
        this.type = type;
    }
}
