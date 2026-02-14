package kasuga.lib.mixins.mixin.create;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import kasuga.lib.KasugaLib;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Mixin(value = GlobalRailwayManager.class, remap = false)
public class GlobalRailwayManagerMixin {
    @Shadow public Map<UUID, TrackGraph> trackNetworks;

    @Inject(method = "loadTrackData", at = @At("TAIL"))
    public void onLoadTrackData(MinecraftServer server, CallbackInfo ci){
        KasugaLib.STACKS.RAILWAY.load(server.overworld());
        KasugaLib.STACKS.RAILWAY.get().syncExtraData(Create.RAILWAYS.trackNetworks.keySet());
        trackNetworks.forEach(((uuid, graph) -> {
            HashSet<TrackEdge> edges = new HashSet<>();
            for (Map<TrackNode, TrackEdge> value : ((TrackGraphAccessor) graph).getConnectionsByNode().values()) {
                for (TrackEdge edge : value.values()) {
                    edges.add(edge);
                }
            }
            KasugaLib.STACKS.RAILWAY.get().withGraph(graph).syncWithExternal(edges);
        }));
    }

    @Inject(method = "putGraph", at = @At("TAIL"))
    public void onPutGraph(TrackGraph graph, CallbackInfo ci){
        if(!((GlobalRailwayManager)(Object)(this) == Create.RAILWAYS))
            return;
        KasugaLib.STACKS.RAILWAY.get().createExtraData(graph.id);
    }

    @Inject(method = "removeGraph", at = @At("TAIL"))
    public void onRemoveGraph(TrackGraph graph, CallbackInfo ci){
        if(!((GlobalRailwayManager)(Object)(this) == Create.RAILWAYS))
            return;
        KasugaLib.STACKS.RAILWAY.get().removeExtraData(graph.id);
    }
}
