package kasuga.lib.example_env.block.gui;

import kasuga.lib.core.javascript.engine.HostAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GuiExampleBlockApi {

    private final GuiExampleBlockEntity blockEntity;

    public GuiExampleBlockApi(GuiExampleBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @HostAccess.Export
    public void incrementData(){
        if(!blockEntity.isRemoved())
            blockEntity.incrementData();
    }
}
