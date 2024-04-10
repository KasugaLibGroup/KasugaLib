package kasuga.lib.example_env.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import interpreter.compute.data.Namespace;
import kasuga.lib.core.client.animation.infrastructure.Condition;
import kasuga.lib.core.client.gui.SimpleScreen;
import kasuga.lib.core.client.gui.enums.ComponentType;
import kasuga.lib.core.client.gui.enums.DisplayType;
import kasuga.lib.core.client.gui.enums.PositionType;
import kasuga.lib.core.client.gui.widget.DataDrivenBox;
import kasuga.lib.core.client.gui.widget.DataDrivenButton;
import kasuga.lib.core.client.gui.widget.DataDrivenComponent;
import kasuga.lib.core.client.render.texture.SimpleTexture;
import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ExampleScreen extends SimpleScreen implements MenuAccess {

    private DataDrivenBox box;

    public ExampleScreen(Component pTitle) {
        super(pTitle);
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {

        box.render(pose,mouseX,mouseY,partialTick);
    }

    @Override
    protected void init() {
        Namespace ns = new Namespace();
        SimpleTexture texture = new SimpleTexture(new ResourceLocation("kasuga_lib","textures/gui/pixel.png"),32,32);
        box = new DataDrivenBox(0,0,100,100, PositionType.FIXED, DisplayType.BLOCK);
        box.setBackground(texture);

        DataDrivenBox containerBox = new DataDrivenBox(0,0,0,0,PositionType.ABSOLUTE,DisplayType.BLOCK);

        containerBox.setLeft(10);
        containerBox.setTop(10);
        containerBox.setRight(10);
        containerBox.setBottom(10);
        containerBox.setBackground(texture);
        box.addChild(containerBox);
        containerBox.setParent(box);

        DataDrivenBox userNameLine = new DataDrivenBox(0,0,0,0,PositionType.STATIC,DisplayType.BLOCK);
        userNameLine.setLeft(0);
        userNameLine.setRight(0);
        userNameLine.setTop(0);
        userNameLine.setHeight(10);
        containerBox.addChild(userNameLine);
        userNameLine.setParent(containerBox);

        DataDrivenComponent usernameLabel = new DataDrivenComponent(0,0,80,10,PositionType.STATIC,ComponentType.LITERAL,DisplayType.BLOCK);
        usernameLabel.content = "Username:";
        usernameLabel.init();
        usernameLabel.setParent(userNameLine);
        userNameLine.addChild(usernameLabel);

        DataDrivenBox passwordLine = new DataDrivenBox(0,0,0,0,PositionType.STATIC,DisplayType.BLOCK);
        passwordLine.setLeft(0);
        passwordLine.setRight(0);
        passwordLine.setTop(0);
        passwordLine.setHeight(10);
        containerBox.addChild(passwordLine);
        passwordLine.setParent(containerBox);

        DataDrivenComponent userNameLabel = new DataDrivenComponent(0,0,80,10,PositionType.STATIC,ComponentType.LITERAL,DisplayType.BLOCK);
        userNameLabel.content = "Password:";
        userNameLabel.init();
        passwordLine.addChild(userNameLabel);
        userNameLabel.setParent(passwordLine);

        DataDrivenButton button = new DataDrivenButton(0,0,0,0,PositionType.STATIC,DisplayType.BLOCK);
        button.setLeft(0);
        button.setRight(0);
        button.setTop(0);
        button.setHeight(20);
        containerBox.addChild(button);
        button.setParent(containerBox);

        box.locate();
    }

    @Override
    public AbstractContainerMenu getMenu() {
        return new ExampleContainer(AllExampleElements.greenApple.getMenuReg().getMenuType(), 0);
    }
}
