package kasuga.lib.mixins.mixin.create;

import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackGraphSync;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import com.simibubi.create.foundation.utility.Pair;
import kasuga.lib.core.create.graph.TrackEdgeLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(value = SignalPropagator.class, remap = false)
public class TestPropagatorMixin {
    @Inject(method = "lambda$propagateSignalGroup$5", at = @At("HEAD"))
    private static void onPropagateSignalGroup$5(Map par1, TrackGraphSync par2, UUID par3, TrackGraph par4, Pair<TrackNode, SignalBoundary> par5, CallbackInfoReturnable ci){
        System.out.printf("[SG] Set Signal Group For Boundary Node %s -> %s\n", par5.getSecond().id, par3);
    }


    @Inject(method = "lambda$propagateSignalGroup$6", at = @At("HEAD"))
    private static void onPropagateSignalGroup$6(Map par1, TrackGraphSync par2, TrackGraph par3, UUID par4, EdgeData par5, CallbackInfoReturnable ci){
        System.out.printf("[SG] Set Signal Group For Edge Node %s -> %s\n", TrackEdgeLocation.fromEdge(((EdgeDataAccessor)par5).getEdge()).toString(), par4);
    }
}
