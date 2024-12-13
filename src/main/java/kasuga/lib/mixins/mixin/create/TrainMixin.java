package kasuga.lib.mixins.mixin.create;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.create.edge_point.BogeyObserverEdgePoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Train.class, remap = false)
public abstract class TrainMixin {

    @Shadow protected abstract void updateNavigationTarget(double distance);

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/trains/entity/Train;updateNavigationTarget(D)V"
            )
    )
    public void doUpdateNavigationTarget(Train train, double distance){
        this.updateNavigationTarget(distance);
        KasugaLib.STACKS.RAILWAY.getIntergartor(train).addDistance(distance);
    }

    @Inject(
            method = "frontSignalListener",
            at = @At("RETURN"),
            cancellable = true
    )
    public void doFrontSignalListener(CallbackInfoReturnable<TravellingPoint.IEdgePointListener> ci){
        TravellingPoint.IEdgePointListener oldListener = ci.getReturnValue();
        ci.setReturnValue((distance, pair)->{
            TrackEdgePoint edgePoint = pair.getFirst();
            if(edgePoint instanceof BogeyObserverEdgePoint observerEdgePoint){
                observerEdgePoint.notifyBogey((Train)(Object)this);
                return false;
            }
            return oldListener.test(distance, pair);
        });
    }

    @Inject(
            method = "backSignalListener",
            at = @At("RETURN"),
            cancellable = true
    )
    public void doBackSignalListener(CallbackInfoReturnable<TravellingPoint.IEdgePointListener> ci){
        TravellingPoint.IEdgePointListener oldListener = ci.getReturnValue();
        ci.setReturnValue((distance, pair)->{
            TrackEdgePoint edgePoint = pair.getFirst();
            if(edgePoint instanceof BogeyObserverEdgePoint observerEdgePoint){
                observerEdgePoint.notifyBogey((Train)(Object)this);
                return false;
            }
            return oldListener.test(distance, pair);
        });
    }
}
