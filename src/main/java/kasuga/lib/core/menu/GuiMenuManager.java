package kasuga.lib.core.menu;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.base.GuiMenuRegistry;
import kasuga.lib.core.menu.javascript.JavascriptMenuRegistry;
import kasuga.lib.core.menu.locator.*;
import kasuga.lib.core.menu.network.GuiMenuNetworking;
import kasuga.lib.core.menu.targets.TargetsClient;

import java.util.*;

public class GuiMenuManager {
    GuiMenuRegistry registry = new GuiMenuRegistry();
    JavascriptMenuRegistry javascriptRegistry = new JavascriptMenuRegistry();
    MenuLocatorRegistry locatorRegistry = new MenuLocatorRegistry();

    HashMap<MenuLocator, List<UUID>> serverKnownData = new HashMap<>();
    HashMap<MenuLocator, List<UUID>> clientKnownData = new HashMap<>();
    Map<MenuLocator, LocatedMenuManager> clientLocators = new HashMap<>();
    Map<MenuLocator, LocatedMenuManager> serverLocators = new HashMap<>();

    public void init(){
        GuiMenuNetworking.invoke();
        locatorRegistry.register(
                KasugaLib.STACKS.REGISTRY.asResource("block"),
                MenuLocatorTypes.CHUNK_MENU
        );
    }

    public void initClient(){
        TargetsClient.register();
    }

    public void initRegistry(){
        KasugaLib.STACKS.JAVASCRIPT.SERVER_REGISTRY.register(
                KasugaLib.STACKS.REGISTRY.asResource("menu"),
                javascriptRegistry
        );
    }


    public void addClientLocator(MenuLocator locator, LocatedMenuManager manager){
        if(this.clientLocators.containsKey(locator)){
            removeClientLocator(locator);
        }
        this.clientLocators.put(locator, manager);
        if(clientKnownData.containsKey(locator)){
            manager.asClient(clientKnownData.get(locator));
        }
    }

    public void removeClientLocator(MenuLocator locator){
        LocatedMenuManager manager = this.clientLocators.remove(locator);
        if(manager != null){
            manager.close();
        }
    }

    public void addServerLocator(MenuLocator locator, LocatedMenuManager manager){
        if(serverLocators.containsKey(locator) && serverLocators.get(locator) != manager){
            removeServerLocator(locator);
        }
        serverLocators.put(locator, manager);
        serverKnownData.put(locator, manager.asServer());
        locator.enable(manager);
    }

    public void removeServerLocator(MenuLocator locator){
        if(serverLocators.containsKey(locator)){
            LocatedMenuManager manager = serverLocators.remove(locator);
            locator.disable(manager);
            manager.close();
        }
        if(serverKnownData.containsKey(locator)){
            serverKnownData.remove(locator);
        }
    }

    public MenuLocatorRegistry getLocatorRegistry() {
        return locatorRegistry;
    }

    public void notifyMenuChange(MenuLocator locator, List<UUID> knownData) {
        if(knownData.size() != 0){
            clientKnownData.put(locator, knownData);
            if(clientLocators.containsKey(locator)){
                clientLocators.get(locator).asClient(knownData);
            }
        }else{
            clientKnownData.remove(locator);
            removeClientLocator(locator);
        }
    }

    public JavascriptMenuRegistry getJavascriptRegistry() {
        return javascriptRegistry;
    }

    protected List<GuiMenu> tickables = new ArrayList<>();

    public void addMenuTickInstance(GuiMenu guiMenu) {
        this.tickables.add(guiMenu);
    }

    public void removeMenuTickInstance(GuiMenu guiMenu) {
        this.tickables.remove(guiMenu);
    }

    public void clientTick(){
        for(GuiMenu menu : tickables){
            menu.clientTick();
        }
    }

    public void reset(){
        List<GuiMenu> menus = List.copyOf(tickables);
        for(GuiMenu menu : menus){
            menu.close();
        }
        tickables.clear();
    }
}
