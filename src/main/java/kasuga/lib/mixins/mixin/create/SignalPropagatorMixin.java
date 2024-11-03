package kasuga.lib.mixins.mixin.create;

import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.signal.SignalPropagator;
import kasuga.lib.core.create.boundary.CustomTrackSegmentPropagator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SignalPropagator.class,remap = false)
public class SignalPropagatorMixin {
    @Inject(method = "notifySignalsOfNewNode", at = @At("RETURN"))
    private static void onNotifySignalsOfNewNode(TrackGraph graph, TrackNode node, CallbackInfo callbackInfo){
        // 增加新结点的时候，一并加入这个结点到CustomBoundary
        CustomTrackSegmentPropagator.notifyNewNode(graph, node);
    }
}
