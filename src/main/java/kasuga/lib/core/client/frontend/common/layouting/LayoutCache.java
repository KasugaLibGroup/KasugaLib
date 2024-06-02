package kasuga.lib.core.client.frontend.common.layouting;

import kasuga.lib.core.util.LazyRecomputable;

public class LayoutCache {
    public LayoutCache tracking = null;

    public LayoutBox engineCoordinate = LayoutBox.ZERO;

    public CacheFunction cacheFunction = CacheFunction.DEFAULT;

    public LazyRecomputable<LayoutBox> screenCoordinate = LazyRecomputable.of(()->cacheFunction.get(tracking,engineCoordinate));

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
        if(engineCoordinate == this.engineCoordinate)
            return;
        this.engineCoordinate = engineCoordinate;
        this.screenCoordinate.clear();
    }
}
