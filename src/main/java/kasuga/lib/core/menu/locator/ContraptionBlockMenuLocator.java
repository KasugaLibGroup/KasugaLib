package kasuga.lib.core.menu.locator;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class ContraptionBlockMenuLocator extends EntityMenuLocator{
    BlockPos blockPos;

    public ContraptionBlockMenuLocator(AbstractContraptionEntity entity, BlockPos blockPos) {
        super(MenuLocatorTypes.CONTRAPTION, entity);
        this.blockPos = blockPos;
    }

    public ContraptionBlockMenuLocator(FriendlyByteBuf byteBuf) {
        super(MenuLocatorTypes.CONTRAPTION, byteBuf);
        this.blockPos = byteBuf.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf byteBuf) {
        super.write(byteBuf);
        byteBuf.writeBlockPos(blockPos);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ContraptionBlockMenuLocator)) return false;
        if (!super.equals(object)) return false;
        ContraptionBlockMenuLocator that = (ContraptionBlockMenuLocator) object;
        return Objects.equals(blockPos, that.blockPos) && Objects.equals(entityId, that.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), blockPos);
    }

    public static ContraptionBlockMenuLocator fromLocator(MovementContext context){
        return new ContraptionBlockMenuLocator(
                context.contraption.entity,
                context.localPos
        );
    }

    public void update(MovementContext context){
        this.transfer(context.contraption.entity.chunkPosition());
    }
}
