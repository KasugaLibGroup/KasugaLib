package kasuga.lib.core.client.model.anim_instance;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;

@OnlyIn(Dist.CLIENT)
public class AnimateTickerManager {

    public static final AnimateTickerManager INSTANCE = new AnimateTickerManager();
    private final HashSet<Ticker> GUI_TICKER, WORLD_TICKER;
    private int guiTick, worldTick;

    public AnimateTickerManager() {
        this.GUI_TICKER = new HashSet<>();
        this.WORLD_TICKER = new HashSet<>();
        guiTick = 0;
        worldTick = 0;
    }

    protected void putTickerIn(Ticker ticker) {
        if (ticker.getType() == AnimateTicker.TickerType.RENDER)
            putGuiTickerIn(ticker);
        else
            putWorldTickerIn(ticker);
    }

    protected void putGuiTickerIn(Ticker ticker) {
        this.GUI_TICKER.add(ticker);
    }

    protected void putWorldTickerIn(Ticker ticker) {
        this.WORLD_TICKER.add(ticker);
    }

    public void tickGui() {
        GUI_TICKER.forEach(t -> t.tick(guiTick));
        this.guiTick ++;
    }

    public void tickWorld() {
        WORLD_TICKER.forEach(t -> t.tick(worldTick));
        this.worldTick ++;
    }

    public int getGuiTick() {
        return guiTick;
    }

    public int getWorldTick() {
        return worldTick;
    }

    public int getTick(AnimateTicker.TickerType type) {
        return type == AnimateTicker.TickerType.RENDER ? getGuiTick() : getWorldTick();
    }

    public void removeTicker(Ticker ticker) {
        if (ticker.getType() == AnimateTicker.TickerType.RENDER)
            GUI_TICKER.remove(ticker);
        else
            WORLD_TICKER.remove(ticker);
    }

    public void resetTicks() {
        this.guiTick = 0;
        this.worldTick = 0;
    }

    public static AnimateTickerManager instance() {
        return INSTANCE;
    }
}
