package kasuga.lib.core.menu;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.menu.base.GuiMenuRegistry;
import kasuga.lib.core.menu.locator.*;
import kasuga.lib.core.menu.network.GuiMenuNetworking;
import kasuga.lib.core.menu.targets.TargetsClient;
import kasuga.lib.core.packets.AllPackets;
import net.minecraft.client.Minecraft;

import java.util.*;

public class GuiMenuManager {
    GuiMenuRegistry registry = new GuiMenuRegistry();
    MenuLocatorRegistry locatorRegistry = new MenuLocatorRegistry();

    HashMap<MenuLocator, List<UUID>> serverKnownData = new HashMap<>();
    HashMap<MenuLocator, List<UUID>> clientKnownData = new HashMap<>();
    Map<MenuLocator, LocatedMenuManager> clientLocators = new HashMap<>();
    Map<MenuLocator, LocatedMenuManager> serverLocators = new HashMap<>();

    public void init(){
        TargetsClient.register();
        GuiMenuNetworking.invoke();
        locatorRegistry.register(
                KasugaLib.STACKS.REGISTRY.asResource("block"),
                MenuLocatorTypes.CHUNK_MENU
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
}
