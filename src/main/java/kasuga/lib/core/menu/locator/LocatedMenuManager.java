package kasuga.lib.core.menu.locator;

import kasuga.lib.core.client.animation.neo_neo.key_frame.KeyFrameHolder;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.base.GuiMenuType;
import kasuga.lib.core.menu.targets.ClientScreenTarget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class LocatedMenuManager {
    private ArrayList<GuiMenuType<?>> menuSuppliers = new ArrayList<>();
    private ArrayList<GuiMenu> menus = new ArrayList<>();

    private ArrayList<Consumer<GuiMenu>> menuInitializers = new ArrayList<>();

    public void close() {
        for (GuiMenu menu : menus) {
            if(menu != null){
                menu.close();
            }
        }
    }

    public void register(GuiMenuType<?> menuType, Consumer<GuiMenu> initializer) {
        this.menuSuppliers.add(menuType);
        this.menuInitializers.add(initializer);
    }

    public ArrayList<UUID> asServer(){
        ArrayList<UUID> uuids = new ArrayList<>();
        for(GuiMenu menu : menus){
            uuids.add(menu.asServer());
        }
        return uuids;
    }

    public void asClient(List<UUID> uuids){
        reset();
        List<UUID> newUuids = new ArrayList<>(uuids);
        for(GuiMenu menu : menus){
            if(uuids.isEmpty()){
                break;
            }
            menu.asClient(newUuids.remove(0));
        }
    }

    public void init(){
        for(int i=0; i<menuSuppliers.size(); i++){
            menus.add(null);
        }
    }

    public void reset(){
        for(int i=0;i<menuSuppliers.size();i++){
            if(i >=0 && i < menus.size() && menus.get(i) != null){
                menus.get(i).close();
            }
            GuiMenu menu = menuSuppliers.get(i).create();
            menuInitializers.get(i).accept(menu);
            menus.set(i, menu);
        }
    }

    public GuiMenu getMenu(int index){
        if(index < 0 || index >= menus.size()){
            return null;
        }
        return menus.get(index);
    }

    public void openScreen(int index){
        GuiMenu menu = getMenu(index);
        if(menu == null){
            return;
        }
        ClientScreenTarget.openScreen(menu);
    }

    public List<UUID> getIdentifiers() {
        List<UUID> uuids = new ArrayList<>();
        for (GuiMenu menu : menus) {
            uuids.add(menu.getId());
        }
        return uuids;
    }

    public static interface Type {
        public LocatedMenuManager create();
    }

    public static class Builder {
        private List<GuiMenuType<?>> menuSuppliers = new ArrayList<>();
        private List<Consumer<GuiMenu>> menuInitializers = new ArrayList<>();

        public Builder with(GuiMenuType<?> menu){
            this.menuSuppliers.add(menu);
            this.menuInitializers.add((m)->{});
            return this;
        }

        public Builder with(GuiMenuType<?> menu, Consumer<GuiMenu> initializer){
            this.menuSuppliers.add(menu);
            this.menuInitializers.add(initializer);
            return this;
        }

        public Type build() {
            return ()->{
                LocatedMenuManager manager =  new LocatedMenuManager();
                for(int i=0;i<menuSuppliers.size();i++){
                    manager.register(menuSuppliers.get(i), menuInitializers.get(i));
                }
                manager.init();
                manager.reset();
                return manager;
            };
        }
    }
}
