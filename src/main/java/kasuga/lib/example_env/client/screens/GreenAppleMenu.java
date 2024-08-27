package kasuga.lib.example_env.client.screens;

import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GreenAppleMenu extends AbstractContainerMenu {
    public GreenAppleMenu(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    public GreenAppleMenu(int containerId, Inventory inventory) {
        this(AllExampleElements.apple.getMenuType(), containerId);
    }

    public GreenAppleMenu(int i, Inventory inventory, FriendlyByteBuf buf) {
        this(i, inventory);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
