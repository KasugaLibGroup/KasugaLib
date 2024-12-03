package kasuga.lib.core.menu.network;

import kasuga.lib.core.menu.IBlockEntityMenuHolder;
import kasuga.lib.core.network.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

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
        this.dimension = byteBuf.readResourceKey(Registries.DIMENSION);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(serverId);
        buffer.writeBlockPos(position);
        buffer.writeResourceKey(dimension);
    }

    @Override
    public void handle(Minecraft minecraft) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()->BlockEntityMenuIdSyncHandler.handle(minecraft, serverId, dimension, position));
    }
}