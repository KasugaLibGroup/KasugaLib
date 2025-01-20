package kasuga.lib.core.client.frontend.gui.events.mouse;

import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.util.data_type.Vec2i;

public class MouseUpEvent extends MouseEvent {
    protected MouseUpEvent(DomNode<?> currentTarget, DomNode<?> target, boolean trusted, Vec2i screenPosition, Vec2i offsetPosition, int button) {
        super(currentTarget, target, trusted, screenPosition, offsetPosition, button);
    }

    public static MouseUpEvent create(DomNode<?> currentTarget, DomNode<?> target, Vec2i screenPosition, Vec2i offsetPosition, int button){
        return new MouseUpEvent(currentTarget, target, true, screenPosition, offsetPosition, button);
    }

    public static MouseUpEvent fromUser(DomNode<?> currentTarget, DomNode<?> target, Vec2i screenPosition, Vec2i offsetPosition, int button){
        return new MouseUpEvent(currentTarget, target, false, screenPosition, offsetPosition, button);
    }

    public static MouseUpEvent fromScreen(DomNode<?> target, Vec2i screenPosition, int button){
        return create(target,target,screenPosition,new Vec2i(0,0),button);
    }

    @Override
    public String getType() {
        return "mouseup";
    }

    @Override
    public MouseEvent forkChild(GuiDomNode guiDomNode, int offsetPositionOffsetX, int offsetPositionOffsetY) {
        return new MouseUpEvent(
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
        return new MouseUpEvent(
                currentTarget,
                target,
                trusted,
                screenPosition,
                offsetPosition,
                button
        );
    }
}
