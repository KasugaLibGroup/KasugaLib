package kasuga.lib.core.client.frontend.gui.events;

import kasuga.lib.core.client.frontend.common.event.DomEvent;
import kasuga.lib.core.client.frontend.common.event.Event;
import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.javascript.engine.HostAccess;
import kasuga.lib.core.util.data_type.Vec2i;

public class MouseEvent extends DomEvent {

    protected final Vec2i screenPosition;
    protected final int button;
    protected final Vec2i offsetPosition;

    protected MouseEvent(
            DomNode<?> currentTarget,
            DomNode<?> target,
            boolean trusted,
            Vec2i screenPosition,
            Vec2i offsetPosition,
            int button
    ) {
        super(currentTarget, target, trusted);
        this.screenPosition = screenPosition;
        this.offsetPosition = offsetPosition;
        this.button = button;
    }

    public static MouseEvent create(DomNode<?> currentTarget, DomNode<?> target, Vec2i screenPosition, Vec2i offsetPosition, int button){
        return new MouseEvent(currentTarget, target, true, screenPosition, offsetPosition, button);
    }

    public static MouseEvent fromUser(DomNode<?> currentTarget, DomNode<?> target, Vec2i screenPosition, Vec2i offsetPosition, int button){
        return new MouseEvent(currentTarget, target, false, screenPosition, offsetPosition, button);
    }

    public static MouseEvent fromScreen(DomNode<?> target, Vec2i screenPosition, int button){
        return create(target,target,screenPosition,new Vec2i(0,0),button);
    }

    public MouseEvent forkChild(GuiDomNode guiDomNode, int offsetPositionOffsetX, int offsetPositionOffsetY) {
        return new MouseEvent(
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
    @HostAccess.Export
    public String getType() {
        return "mouse";
    }

    @HostAccess.Export
    public Vec2i getScreenPosition(){
        return screenPosition;
    }

    @HostAccess.Export
    public Vec2i getOffsetPosition(){
        return offsetPosition;
    }

    public MouseEvent withTarget(GuiDomNode target) {
        return create(currentTarget, target, screenPosition, offsetPosition, button);
    }
}
