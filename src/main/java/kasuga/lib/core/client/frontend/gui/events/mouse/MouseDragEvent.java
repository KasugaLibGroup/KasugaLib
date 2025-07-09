package kasuga.lib.core.client.frontend.gui.events.mouse;

import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;
import kasuga.lib.core.util.data_type.Vec2i;
import net.minecraft.world.phys.Vec3;

public class MouseDragEvent extends MouseEvent {

    public final Vec2i delta;


    protected MouseDragEvent(DomNode<?> currentTarget, DomNode<?> target, boolean trusted, Vec2i screenPosition, Vec2i offsetPosition, int button, Vec2i delta) {
        super(currentTarget, target, trusted, screenPosition, offsetPosition, button);
        this.delta = delta;
    }

    public static MouseDragEvent create(DomNode<?> currentTarget, DomNode<?> target, Vec2i screenPosition, Vec2i offsetPosition, int button, Vec2i delta){
        return new MouseDragEvent(currentTarget, target, true, screenPosition, offsetPosition, button, delta);
    }

    public static MouseDragEvent fromUser(DomNode<?> currentTarget, DomNode<?> target, Vec2i screenPosition, Vec2i offsetPosition, int button, Vec2i delta){
        return new MouseDragEvent(currentTarget, target, false, screenPosition, offsetPosition, button, delta);
    }

    public static MouseDragEvent fromScreen(DomNode<?> target, Vec2i screenPosition, int button, Vec2i delta){
        return create(target,target,screenPosition,new Vec2i(0,0),button, delta);
    }

    @Override
    public String getType() {
        return "mousedrag";
    }

    @Override
    public MouseEvent forkChild(GuiDomNode guiDomNode, int offsetPositionOffsetX, int offsetPositionOffsetY) {
        return new MouseDragEvent(
                target,
                guiDomNode,
                trusted,
                screenPosition,
                new Vec2i(
                        offsetPositionOffsetX,
                        offsetPositionOffsetY
                ),
                button,
                delta
        );
    }

    @Override
    public MouseEvent withTarget(GuiDomNode target) {
        return new MouseDragEvent(
                currentTarget,
                target,
                trusted,
                screenPosition,
                offsetPosition,
                button,
                delta
        );
    }

    @HostAccess.Export
    public Vec2i getDelta() {
        return delta;
    }
}
