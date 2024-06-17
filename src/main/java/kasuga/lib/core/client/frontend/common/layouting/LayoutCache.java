package kasuga.lib.core.client.frontend.common.layouting;

import kasuga.lib.core.util.LazyRecomputable;

import java.util.Objects;
import java.util.WeakHashMap;

public class LayoutCache {
    public LayoutCache tracking = null;

    public WeakHashMap<LayoutCache,Boolean> updated = new WeakHashMap<>();

    public LayoutBox engineCoordinate = LayoutBox.ZERO;

    public CacheFunction cacheFunction = CacheFunction.DEFAULT;

    public LazyRecomputable<LayoutBox> screenCoordinate = LazyRecomputable.predictable(
            ()->{
                if(tracking != null)
                    tracking.notifyUpdated(this);
                updated.clear();
                return cacheFunction.get(tracking,engineCoordinate);
            },
            ()->tracking == null || !tracking.shouldUpdate(this)
    );

    private void notifyUpdated(LayoutCache layoutCache) {
        updated.put(layoutCache,false);
    }

    private boolean shouldUpdate(LayoutCache layoutCache) {
        return updated.getOrDefault(layoutCache,true);
    }

    @FunctionalInterface
    public static interface CacheFunction{
        public LayoutBox get(LayoutCache tracking, LayoutBox engineCoordinate);
        public static CacheFunction DEFAULT = new CacheFunction() {
            @Override
            public LayoutBox get(LayoutCache tracking, LayoutBox engineCoordinate) {
                LayoutBox trackingScreenCoordinate = tracking == null ? LayoutBox.ZERO : tracking.screenCoordinate.get();
                return engineCoordinate.addCoordinateFrom(trackingScreenCoordinate);
            }
        };
    }

    public void setTracking(LayoutCache tracking) {
        if(tracking == this.tracking)
            return;
        this.tracking = tracking;
        this.screenCoordinate.clear();
    }

    public void setCacheFunction(CacheFunction cacheFunction) {
        if(cacheFunction == this.cacheFunction)
            return;
        this.cacheFunction = cacheFunction;
        this.screenCoordinate.clear();
    }

    public void setEngineCoordinate(LayoutBox engineCoordinate) {
        if(Objects.equals(engineCoordinate,this.engineCoordinate))
            return;
        this.engineCoordinate = engineCoordinate;
        this.screenCoordinate.clear();
    }
}
