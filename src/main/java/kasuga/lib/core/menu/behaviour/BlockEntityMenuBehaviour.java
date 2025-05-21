package kasuga.lib.core.menu.behaviour;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import kasuga.lib.core.menu.IBlockEntityMenuHolder;
import kasuga.lib.core.menu.api.GuiMenuUtils;
import kasuga.lib.core.menu.base.GuiMenu;
import kasuga.lib.core.menu.network.BlockEntityMenuIdSyncPacket;
import kasuga.lib.core.packets.AllPackets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class BlockEntityMenuBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<BlockEntityMenuBehaviour> TYPE = new BehaviourType<>();

    private final GuiMenu menuEntry;
    private UUID serverId;
    private final Supplier<GuiMenu> menuSupplier;

    public BlockEntityMenuBehaviour(SmartBlockEntity be, Supplier<GuiMenu> menuSupplier) {
        super(be);
        this.menuSupplier = menuSupplier;
        this.menuEntry = menuSupplier.get();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void initialize() {
        Level level = getWorld();
        if (level != null && level instanceof ServerLevel) {
            menuEntry.asServer();
            sendMenuIdUpdate();
        }
    }

    public void sendMenuIdUpdate() {
        Level level = getWorld();
        if (level instanceof ServerLevel) {
            UUID serverId = menuEntry.asServer();
            BlockEntityMenuIdSyncPacket packet = new BlockEntityMenuIdSyncPacket(
                serverId,
                getPos(),
                level.dimension()
            );
            AllPackets.CHANNEL_REG.getChannel().send(
                PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(getPos())),
                packet
            );
        }
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        if(!clientPacket)
            return;
        if (getWorld() instanceof ServerLevel) {
            nbt.putUUID("menuId", menuEntry.getServerId());
        }
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        if(!clientPacket)
            return;
        if (nbt.hasUUID("menuId")) {
            serverId = nbt.getUUID("menuId");
            notifyMenuId(serverId);
        }
    }

    public void notifyMenuId(UUID menuId) {
        if (menuId.equals(this.menuEntry.getServerId()))
            return;
        if (this.menuEntry != null) {
            this.menuEntry.close();
        }
        GuiMenu newMenu = menuSupplier.get();
        newMenu.asClient(menuId);
    }

    public void openScreen() {
        GuiMenuUtils.openScreen(menuEntry);
    }

    @Override
    public void destroy() {
        menuEntry.close();
    }

    public GuiMenu getMenuEntry() {
        return menuEntry;
    }
} 