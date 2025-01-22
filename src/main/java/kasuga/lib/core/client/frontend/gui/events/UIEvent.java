package kasuga.lib.core.client.frontend.gui.events;

import kasuga.lib.core.client.frontend.common.event.DomEvent;
import kasuga.lib.core.client.frontend.dom.nodes.DomNode;

public abstract class UIEvent extends DomEvent {
    protected UIEvent(DomNode<?> currentTarget, DomNode<?> target, boolean trusted) {
        super(currentTarget, target, trusted);
    }
}
