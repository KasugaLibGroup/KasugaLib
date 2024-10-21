package kasuga.lib.registrations.common;

import com.tterrag.registrate.builders.MenuBuilder;
import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.core.annos.Optional;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Menu is the base class of minecraft RENDER. You could use it to open the player's screen and create container guis.
 * For more info, see {@link AbstractContainerMenu} and {@link Screen},
 * In order to create a menu, your game element should be a subClass of {@link net.minecraft.world.MenuProvider}
 * @param <T> your menu class.
 * @param <U> your screen class.
 */
public class MenuReg<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> extends Reg {
    private IContainerFactory<T> menuFactory;
    private Supplier<FullScreenInvoker<T, U>> screenFactory;
    private RegistryObject<MenuType<T>> registryObject = null;

    /**
     * Create a menu reg.
     * @param registrationKey the registration key of your menu.
     */
    public MenuReg(String registrationKey) {
        super(registrationKey);
    }

    /**
     * load a constructor for your menu and screen.
     * @param menu Your menu's constructor lambda.
     * @param screen Your screen's constructor lambda.
     * @return self.
     */
    @Optional
    public MenuReg<T, U>
    withMenuAndScreen(IContainerFactory<T> menu, Supplier<FullScreenInvoker<T, U>> screen) {
        this.menuFactory = menu;
        this.screenFactory = screen;
        return this;
    }

    /**
     * Submit your config to minecraft registry.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Override
    @Mandatory
    public MenuReg<T, U> submit(SimpleRegistry registry) {
        registry.cacheMenuIn(this);
        if (menuFactory == null) {
            crashOnNotPresent(IContainerFactory.class, "withMenuAndScreen", "submit");
        }
        if (screenFactory == null) {
            crashOnNotPresent(Screen.class, "withMenuAndScreen", "submit");
        }
        if (registryObject != null) return this;
        this.registryObject = registry.menus().register(registrationKey, () -> IForgeMenuType.create(menuFactory));
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

    @OnlyIn(Dist.CLIENT)
    public void hookMenuAndScreen() {
        MenuScreens.register(registryObject.get(), screenFactory.get().getConstructor());
    }

    public interface ScreenInvoker<U extends Screen> {
        U invoke(Component title);
    }

    public interface FullScreenInvoker<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U invoke(T menu, Inventory pInventory, Component pTitle);

        @OnlyIn(Dist.CLIENT)
        default MenuScreens.ScreenConstructor<T, U> getConstructor() {
            return this::invoke;
        }
    }
}
