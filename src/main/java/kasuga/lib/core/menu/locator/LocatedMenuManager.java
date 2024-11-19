package kasuga.lib.core.menu.locator;

import kasuga.lib.core.client.animation.neo_neo.key_frame.KeyFrameHolder;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.base.GuiMenuType;
import kasuga.lib.core.menu.targets.ClientScreenTarget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocatedMenuManager {
    private ArrayList<GuiMenuType<?>> menuSuppliers = new ArrayList<>();
    private ArrayList<GuiMenu> menus = new ArrayList<>();

    public void close() {
        for (GuiMenu menu : menus) {
            if(menu != null){
                menu.close();
            }
        }
    }

    public void register(GuiMenuType<?> menuType) {
        this.menuSuppliers.add(menuType);
    }

    public ArrayList<UUID> asServer(){
        reset();
        ArrayList<UUID> uuids = new ArrayList<>();
        for(GuiMenu menu : menus){
            uuids.add(menu.asServer());
        }
        return uuids;
    }

    public void asClient(List<UUID> uuids){
        reset();
        for(GuiMenu menu : menus){
            if(uuids.isEmpty()){
                break;
            }
            menu.asClient(uuids.remove(0));
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
            menus.set(i, menuSuppliers.get(i).create());
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

        public Builder with(GuiMenuType<?> menu){
            this.menuSuppliers.add(menu);
            return this;
        }
        public Type build() {
            return ()->{
                LocatedMenuManager manager =  new LocatedMenuManager();
                for(GuiMenuType<?> menu : menuSuppliers){
                    manager.register(menu);
                }
                manager.init();
                return manager;
            };
        }
    }
}
