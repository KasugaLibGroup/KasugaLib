package kasuga.lib.core.client.frontend.gui.events.mouse;

import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.util.data_type.Vec2i;

public class MouseClickEvent extends MouseEvent {
    protected MouseClickEvent(DomNode<?> currentTarget, DomNode<?> target, boolean trusted, Vec2i screenPosition, Vec2i offsetPosition, int button) {
        super(currentTarget, target, trusted, screenPosition, offsetPosition, button);
    }

    public static MouseClickEvent create(DomNode<?> currentTarget, DomNode<?> target, Vec2i screenPosition, Vec2i offsetPosition, int button){
        return new MouseClickEvent(currentTarget, target, true, screenPosition, offsetPosition, button);
    }

    public static MouseClickEvent fromUser(DomNode<?> currentTarget, DomNode<?> target, Vec2i screenPosition, Vec2i offsetPosition, int button){
        return new MouseClickEvent(currentTarget, target, false, screenPosition, offsetPosition, button);
    }

    public static MouseClickEvent fromScreen(DomNode<?> target, Vec2i screenPosition, int button){
        return create(target,target,screenPosition,new Vec2i(0,0),button);
    }

    @Override
    public String getType() {
        return "click";
    }

    @Override
    public MouseEvent forkChild(GuiDomNode guiDomNode, int offsetPositionOffsetX, int offsetPositionOffsetY) {
        return new MouseClickEvent(
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
        return new MouseClickEvent(
                currentTarget,
                target,
                trusted,
                screenPosition,
                offsetPosition,
                button
        );
    }
}
