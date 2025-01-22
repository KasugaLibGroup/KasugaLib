package kasuga.lib.core.client.frontend.gui.events.mouse;

import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.util.data_type.Vec2i;

public class MouseDownEvent extends MouseEvent {
    protected MouseDownEvent(DomNode<?> currentTarget, DomNode<?> target, boolean trusted, Vec2i screenPosition, Vec2i offsetPosition, int button) {
        super(currentTarget, target, trusted, screenPosition, offsetPosition, button);
    }

    public static MouseDownEvent create(DomNode<?> currentTarget, DomNode<?> target, Vec2i screenPosition, Vec2i offsetPosition, int button){
        return new MouseDownEvent(currentTarget, target, true, screenPosition, offsetPosition, button);
    }

    public static MouseDownEvent fromUser(DomNode<?> currentTarget, DomNode<?> target, Vec2i screenPosition, Vec2i offsetPosition, int button){
        return new MouseDownEvent(currentTarget, target, false, screenPosition, offsetPosition, button);
    }

    public static MouseDownEvent fromScreen(DomNode<?> target, Vec2i screenPosition, int button){
        return create(target,target,screenPosition,new Vec2i(0,0),button);
    }

    @Override
    public String getType() {
        return "mousedown";
    }

    @Override
    public MouseEvent forkChild(GuiDomNode guiDomNode, int offsetPositionOffsetX, int offsetPositionOffsetY) {
        return new MouseDownEvent(
                target,
                guiDomNode,
                trusted,
                screenPosition,
                new Vec2i(
                        offsetPositionOffsetX,
                        offsetPositionOffsetY
                ),
                button
        );
    }

    @Override
    public MouseEvent withTarget(GuiDomNode target) {
        return new MouseDownEvent(
                currentTarget,
                target,
                trusted,
                screenPosition,
                offsetPosition,
                button
        );
    }
}
