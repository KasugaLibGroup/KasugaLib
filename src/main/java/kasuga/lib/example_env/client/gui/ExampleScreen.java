package kasuga.lib.example_env.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import interpreter.compute.data.Namespace;
import kasuga.lib.core.client.animation.infrastructure.Condition;
import kasuga.lib.core.client.gui.SimpleScreen;
import kasuga.lib.core.client.gui.enums.DisplayType;
import kasuga.lib.core.client.gui.enums.PositionType;
import kasuga.lib.core.client.gui.widget.DataDrivenBox;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ExampleScreen extends SimpleScreen implements MenuAccess {

    private final DataDrivenBox box;

    public ExampleScreen(Component pTitle) {
        super(pTitle);
        Namespace ns = new Namespace();
        SimpleTexture texture = new SimpleTexture(new ResourceLocation("kasuga_lib","textures/gui/pixel.png"),32,32);
        box = new DataDrivenBox(0,0,100,100, PositionType.FIXED, DisplayType.BLOCK);
        box.setBackground(texture);
        box.setBackgroundWidth(10);
        box.setBackgroundHeight(10);
        for (int i = 0; i < 15; i++) {
            DataDrivenBox childBox = new DataDrivenBox(0,0,10,10+i,PositionType.STATIC,DisplayType.INLINE_BLOCK);
            childBox.setBackground(texture);
            childBox.setBackgroundWidth(10);
            childBox.setBackgroundHeight(10);
            childBox.setParent(box);
            box.addChild(childBox);
        }

        DataDrivenBox childBox = new DataDrivenBox(0,0,10,20,PositionType.STATIC,DisplayType.BLOCK);
        childBox.setBackground(texture);
        childBox.setBackgroundWidth(10);
        childBox.setBackgroundHeight(10);
        childBox.setParent(box);
        box.addChild(childBox);
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        box.locate();
        box.render(pose,mouseX,mouseY,partialTick);
    }

    @Override
    public AbstractContainerMenu getMenu() {
        return new ExampleContainer(AllExampleElements.greenApple.getMenuReg().getMenuType(), 0);
    }
}
