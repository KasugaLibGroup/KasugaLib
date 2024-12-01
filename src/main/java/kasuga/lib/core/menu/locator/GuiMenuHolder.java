package kasuga.lib.core.menu.locator;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.base.GuiMenuType;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class GuiMenuHolder {
    private final List<GuiMenu> menus = new ArrayList<>();
    private LocatedMenuManager manager;
    private MenuLocator locator;
    private final LocatedMenuManager.Type managerType;

    private boolean isServer = false;
    private boolean isEnabled = false;
    
    private GuiMenuHolder(LocatedMenuManager.Type managerType, MenuLocator locator) {
        this.managerType = managerType;
        this.locator = locator;
    }

    public void enableServer(MenuLocator locator) {
        if(this.manager != null) {
            return;
        }
        isEnabled = true;
        isServer = true;
        this.manager = managerType.create();
        manager.asServer();
        KasugaLib.STACKS.MENU.addServerLocator(locator, manager);
    }

    public void enableClient(MenuLocator locator) {
        if(this.manager != null) {
            return;
        }
        isEnabled = true;
        isServer = false;
        this.manager = managerType.create();
        KasugaLib.STACKS.MENU.addClientLocator(locator, manager);
    }

    public void enable(LevelAccessor level) {
        if(isEnabled)
            return;
        if(level.isClientSide()) {
            enableClient(locator);
        } else {
            enableServer(locator);
        }
    }
    
    public void disable() {
        if(this.manager == null || !isEnabled) {
            return;
        }
        if(this.locator != null) {
            if(isServer) {
                KasugaLib.STACKS.MENU.removeServerLocator(locator);
            } else {
                KasugaLib.STACKS.MENU.removeClientLocator(locator);
            }
        }
        this.manager = null;
        this.locator = null;
        this.isEnabled = false;
        this.isServer = false;
    }

    public Optional<GuiMenu> getMenu(int index) {
        if(manager == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(manager.getMenu(index));
    }

    public void openScreen(int index) {
        if(manager == null) {
            return;
        }
        manager.openScreen(index);
    }
    
    public static class Builder {
        private final LocatedMenuManager.Builder managerBuilder = new LocatedMenuManager.Builder();
        private MenuLocator locator;

        public Builder with(GuiMenuType<?> menuType) {
            managerBuilder.with(menuType);

            return this;
        }

        public <T extends GuiMenu> Builder with(GuiMenuType<T> menuType, Consumer<T> initialParameter) {
            managerBuilder.with(menuType, (Consumer<GuiMenu>) initialParameter);
            return this;
        }

        public Builder locatedAt(MenuLocator locator) {
            this.locator = locator;
            return this;
        }
        
        public GuiMenuHolder build() {
            if(locator == null) {
                throw new IllegalStateException("Locator must be set");
            }
            return new GuiMenuHolder(managerBuilder.build(),locator);
        }
    }
} 