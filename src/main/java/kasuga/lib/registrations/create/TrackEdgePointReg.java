package kasuga.lib.registrations.create;

import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import kasuga.lib.core.create.edge_point.EdgePointOverlayRenderer;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.builders.SelfReferenceItemBuilder;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class TrackEdgePointReg<T extends TrackEdgePoint> extends Reg {

    private Supplier<T> factory;
    private EdgePointType<T> type;

    public TrackEdgePointReg(String registrationKey) {
        super(registrationKey);
    }

    @Override
    public TrackEdgePointReg<T> submit(SimpleRegistry registry) {
        type = EdgePointType.register(registry.asResource(registrationKey), factory);
        return this;
    }

    public TrackEdgePointReg<T> use(Supplier<T> factory){
        this.factory = factory;
        return this;
    }

    public TrackEdgePointReg<T> withRenderer(Supplier<EdgePointOverlayRenderer> rendererSupplier){
        // @TODO: WIP
        return this;
    }

    public EdgePointType<T> getType(){
        return type;
    }

    public <T extends Block> SelfReferenceItemBuilder<?,T> getBlockItemFactory(){
        NonNullBiFunction<? super Block, Item.Properties, TrackTargetingBlockItem> apply = TrackTargetingBlockItem.ofType(type);
        return (i,j)->(Item) apply.apply(i,j);
    }

    @Override
    public String getIdentifier() {
        return "track_edge_point";
    }
}
