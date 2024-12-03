package kasuga.lib.core.menu.network;

import kasuga.lib.core.menu.IBlockEntityMenuHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class BlockEntityMenuIdSyncHandler {
    public static void handle(Minecraft minecraft, UUID serverId, ResourceKey<Level> dimension, BlockPos position){
        Level world = minecraft.level;
        if (world != null && world.dimension() == dimension) {
            if (world.getBlockEntity(position) instanceof IBlockEntityMenuHolder menuHolder) {
                menuHolder.notifyMenuId(serverId);
            }
        }
    }
}
