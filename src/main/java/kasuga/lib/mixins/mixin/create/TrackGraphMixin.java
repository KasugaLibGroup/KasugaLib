package kasuga.lib.mixins.mixin.create;

import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.track.BezierConnection;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.create.graph.GraphExtraData;
import net.createmod.catnip.data.Couple;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

@Mixin(value = TrackGraph.class, remap = false)
public abstract class TrackGraphMixin {
    @Shadow public abstract TrackNode getNode(int netId);

    @Shadow public abstract TrackNode locateNode(TrackNodeLocation position);

    @Shadow public abstract Map<TrackNode, TrackEdge> getConnectionsFrom(TrackNode node);

    @Shadow private Map<TrackNode, Map<TrackNode, TrackEdge>> connectionsByNode;

    @Inject(method = "transferAll", at = @At("TAIL"))
    public void onTransferAll(TrackGraph toOther, CallbackInfo ci){
        GraphExtraData toOtherExtra = KasugaLib.STACKS.RAILWAY.get().withGraph(toOther);
        KasugaLib.STACKS.RAILWAY.get().withGraph(((TrackGraph)(Object)this)).transferAll(toOtherExtra);
    }

    @Inject(method = "transfer", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onTransfer(
            LevelAccessor level,
            TrackNode node,
            TrackGraph target,
            CallbackInfo ci
    ){
        if(level.isClientSide())
            return;
        Map<TrackNode, TrackEdge> connections = this.getConnectionsFrom(node);
        GraphExtraData targetExtraData = KasugaLib.STACKS.RAILWAY.get().withGraph(target);
        KasugaLib.STACKS.RAILWAY.get().withGraph(((TrackGraph)(Object)this)).transfer(level, node, connections, targetExtraData);
    }

    @Inject(method = "connectNodes", at = @At("TAIL"))
    void onConnectNodes(
            LevelAccessor reader,
            TrackNodeLocation.DiscoveredLocation location,
            TrackNodeLocation.DiscoveredLocation location2,
            @Nullable BezierConnection turn,
            CallbackInfo ci
    ){
        if(reader.isClientSide()){
            return;
        }
        TrackNode node1 = locateNode(location);
        TrackNode node2 = locateNode(location2);
        TrackEdge edge = ((TrackGraph)(Object)this).getConnection(Couple.create(node1, node2));
        TrackEdge edgeReverse = ((TrackGraph)(Object)this).getConnection(Couple.create(node2, node1));
        KasugaLib.STACKS.RAILWAY.get().withGraph(((TrackGraph)(Object)this)).createEdge(edge);
        KasugaLib.STACKS.RAILWAY.get().withGraph(((TrackGraph)(Object)this)).createEdge(edgeReverse);
    }

    @Inject(method = "removeNode", at = @At("HEAD"))
    void onRemoveNodes(
            @Nullable LevelAccessor reader,
            TrackNodeLocation location,
            CallbackInfoReturnable ci
    ){
        if(reader == null || reader.isClientSide())
            return;
        TrackNode node = locateNode(location);
        if(node == null)
            return;
        Map<TrackNode, TrackEdge> connections = getConnectionsFrom(node);
        connections.forEach((_node,edge)->{
            KasugaLib.STACKS.RAILWAY.get().withGraph(((TrackGraph)(Object)this)).removeEdge(edge);
        });
        // @TODO: Create's code, add MIT's LICENSE
        for (TrackNode fromNodes : connections.keySet())
            if (connectionsByNode.containsKey(fromNodes)) {
                TrackEdge edge = connectionsByNode.get(fromNodes).get(node);
                KasugaLib.STACKS.RAILWAY.get().withGraph(((TrackGraph) (Object) this)).removeEdge(edge);
            }
    }
}
