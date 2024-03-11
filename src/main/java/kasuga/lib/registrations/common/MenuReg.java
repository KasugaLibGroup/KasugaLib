package kasuga.lib.registrations.common;

import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;

public class MenuReg<T extends AbstractContainerMenu, F extends Screen, U extends Screen & MenuAccess<T>> extends Reg {
    private IContainerFactory<T> menuFactory;
    private MenuScreens.ScreenConstructor<T, U> screenFactory;
    private RegistryObject<MenuType<T>> registryObject = null;

    public MenuReg(String registrationKey) {
        super(registrationKey);
    }

    public MenuReg<T, F, U>
    withMenuAndScreen(IContainerFactory<T> menu, MenuScreens.ScreenConstructor<T, U> screen) {
        this.menuFactory = menu;
        this.screenFactory = screen;
        return this;
    }

    @Override
    public MenuReg<T, F, U> submit(SimpleRegistry registry) {
        if(menuFactory == null) return this;
        this.registryObject = registry.menus().register(registrationKey, () -> IForgeMenuType.create(menuFactory));
        if(screenFactory == null) return this;
        MenuScreens.register(registryObject.get(), screenFactory);
        return this;
    }

    public RegistryObject<MenuType<T>> getRegistryObject() {
        return registryObject;
    }

    public MenuType<T> getMenuType() {
        if(registryObject == null) return null;
        return registryObject.get();
    }

    @Override
    public String getIdentifier() {
        return "menu";
    }
}
