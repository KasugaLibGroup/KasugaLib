package kasuga.lib.core.menu.network;

import kasuga.lib.core.menu.IBlockEntityMenuHolder;
import kasuga.lib.core.menu.behaviour.BlockEntityMenuBehaviour;
import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

public class BlockEntityMenuIdSyncPacket extends S2CPacket {
    private final UUID serverId;
    private final BlockPos position;
    private final ResourceKey<Level> dimension;

    public BlockEntityMenuIdSyncPacket(UUID serverId, BlockPos position, ResourceKey<Level> dimension) {
        this.serverId = serverId;
        this.position = position;
        this.dimension = dimension;
    }

    public BlockEntityMenuIdSyncPacket(FriendlyByteBuf byteBuf) {
        this.serverId = byteBuf.readUUID();
        this.position = byteBuf.readBlockPos();
        this.dimension = byteBuf.readResourceKey(Registry.DIMENSION_REGISTRY);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(serverId);
        buffer.writeBlockPos(position);
        buffer.writeResourceKey(dimension);
    }

    @Override
    public void handle(Minecraft minecraft) {
        Level world = minecraft.level;
        if (world != null && world.dimension() == dimension) {
            BlockEntity blockEntity = world.getBlockEntity(position);
            BlockEntityMenuBehaviour behaviour = BlockEntityMenuBehaviour.get(blockEntity, BlockEntityMenuBehaviour.TYPE);
            if (behaviour != null) {
                behaviour.notifyMenuId(serverId);
                return;
            }
            if (world.getBlockEntity(position) instanceof IBlockEntityMenuHolder menuHolder) {
                menuHolder.notifyMenuId(serverId);
            }
        }
    }
}