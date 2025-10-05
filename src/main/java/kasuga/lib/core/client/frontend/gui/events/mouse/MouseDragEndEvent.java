package kasuga.lib.core.client.frontend.gui.events.mouse;

import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.util.data_type.Vec2i;

public class MouseDragEndEvent extends MouseEvent{

    protected MouseDragEndEvent(DomNode<?> currentTarget, DomNode<?> target, boolean trusted, Vec2i screenPosition, Vec2i offsetPosition, int button) {
        super(currentTarget, target, trusted, screenPosition, offsetPosition, button);
    }

    public static MouseDragEndEvent fromMouseUp(MouseUpEvent event) {
        return new MouseDragEndEvent(
                event.getCurrentTarget(),
                event.getTarget(),
                event.isTrusted(),
                event.getScreenPosition(),
                event.getOffsetPosition(),
                event.getButton()
        );
    }

    @Override
    public String getType() {
        return "mousedragend";
    }

    @Override
    public MouseDragEndEvent withTarget(GuiDomNode target) {
        return new MouseDragEndEvent(
                target,
                getTarget(),
                isTrusted(),
                getScreenPosition(),
                getOffsetPosition(),
                getButton()
        );
    }
}
