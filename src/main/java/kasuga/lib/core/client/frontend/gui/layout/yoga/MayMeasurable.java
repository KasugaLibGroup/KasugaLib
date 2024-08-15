package kasuga.lib.core.client.frontend.gui.layout.yoga;

import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaMeasureFunction;

import java.util.Optional;

public interface MayMeasurable {
    public Optional<YogaMeasureFunction> measure();
}
