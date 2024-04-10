package kasuga.lib.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.enums.DisplayType;
import kasuga.lib.core.client.gui.enums.PositionType;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class DataDrivenButton extends DataDrivenWidget{
    public DataDrivenButton(int pX, int pY, int pWidth, int pHeight, PositionType type, DisplayType displayType) {
        super(pX, pY, pWidth, pHeight, type, displayType);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.updatePosition();
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public Component getMessage() {
        return Component.literal("OK");
    }
}
