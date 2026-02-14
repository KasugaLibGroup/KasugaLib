package kasuga.lib.mixins.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.create.device.TrainDeviceManager;
import kasuga.lib.core.create.device.TrainDeviceProvider;
import kasuga.lib.core.create.edge_point.BogeyObserverEdgePoint;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mixin(value = Train.class, remap = false)
public class TrainMixin implements TrainDeviceProvider {

    @Unique
    private TrainDeviceManager kasuga$manager;

    @Unique
    protected void updateNavigationTarget(double distance){throw new IllegalStateException("Mixin Error?");}

    @Shadow public double speed;

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Train;tickPassiveSlowdown()V")
    )
    public void doTickPassiveSlowdown(Train train, Operation<Train> original) {
        if (kasuga$manager == null || !kasuga$manager.cancelSlowdown()) {
            original.call(train);
        }

        Optional<Double> speed = kasuga$manager.beforeSpeed();

        speed.ifPresent(aDouble -> this.speed = aDouble);
    }



    @Unique
    public void doUpdateNavigationTarget(Train train, double distance){
        if(train.graph == null)return;
        this.updateNavigationTarget(distance);
        KasugaLib.STACKS.RAILWAY.getIntergartor(train).addDistance(distance);
        if(kasuga$manager != null) {
            kasuga$manager.notifySpeed(speed);
            kasuga$manager.notifyDistance(distance);
        }
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
            if(kasuga$manager != null && kasuga$manager.notifySingalFront(edgePoint)) return true;
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
            if(kasuga$manager != null && kasuga$manager.notifySingalBack(edgePoint)) return false;
            return oldListener.test(distance, pair);
        });
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onConstruct(UUID id, UUID owner, TrackGraph graph, List<Carriage> carriages, List<Integer> carriageSpacing, boolean doubleEnded, CallbackInfo ci) {
        this.kasuga$manager = new TrainDeviceManager((Train) (Object) this);
    }

    @Inject(method = "read", at = @At("RETURN"))
    private static void onRead(CompoundTag tag, Map<UUID, TrackGraph> trackNetworks, DimensionPalette dimensions, CallbackInfoReturnable<Train> ci) {
        ((TrainDeviceProvider)ci.getReturnValue()).getDeviceManager().read(tag);
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void onWrite(DimensionPalette dimensions, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        this.kasuga$manager.write(tag);
    }

    @Override
    public TrainDeviceManager getDeviceManager() {
        return this.kasuga$manager;
    }
}