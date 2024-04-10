package kasuga.lib.example_env.client.gui;

import kasuga.lib.example_env.AllExampleElements;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ExampleContainer extends AbstractContainerMenu {

    public ExampleContainer(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    public ExampleContainer(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AllExampleElements.greenApple.getMenuReg().getMenuType(), windowId);
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
