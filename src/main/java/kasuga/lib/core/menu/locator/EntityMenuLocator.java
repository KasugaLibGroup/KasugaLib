package kasuga.lib.core.menu.locator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

import java.util.Objects;
import java.util.UUID;

public class EntityMenuLocator extends AbstractDynamicChunkBasedLocator {
    protected final UUID entityId;
    protected Entity entity;
    protected EntityMenuLocator(MenuLocatorType<?> type, Entity entity) {
        super(type, entity.chunkPosition());
        this.entityId = entity.getUUID();
    }

    public EntityMenuLocator(Entity entity) {
        super(MenuLocatorTypes.ENTITY, entity.chunkPosition());
        this.entityId = entity.getUUID();
    }

    protected EntityMenuLocator(MenuLocatorType<?> type, FriendlyByteBuf byteBuf) {
        super(type, byteBuf);
        this.entityId = byteBuf.readUUID();
    }

    public EntityMenuLocator(FriendlyByteBuf byteBuf) {
        super(MenuLocatorTypes.ENTITY, byteBuf);
        this.entityId = byteBuf.readUUID();
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        super.write(byteBuf);
        byteBuf.writeUUID(entityId);
    }

    public void tick(Entity entity){
        transfer(entity.chunkPosition());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof EntityMenuLocator)) return false;
        EntityMenuLocator that = (EntityMenuLocator) object;
        return Objects.equals(entityId, that.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId);
    }
}
