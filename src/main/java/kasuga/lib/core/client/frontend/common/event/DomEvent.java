package kasuga.lib.core.client.frontend.common.event;

import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;

public abstract class DomEvent extends Event{
    protected final DomNode<?> currentTarget;
    protected final DomNode<?> target;
    protected boolean propagationStopped;
    protected DomEvent(DomNode<?> currentTarget, DomNode<?> target, boolean trusted) {
        super(trusted);
        this.currentTarget = currentTarget;
        this.target = target;
    }
    @HostAccess.Export
    public void stopPropagation(){
        this.propagationStopped = true;
    }

    @HostAccess.Export
    public boolean isPropagationStopped(){
        return propagationStopped;
    }
    @HostAccess.Export
    public DomNode<?> getCurrentTarget(){
        return currentTarget;
    }

    @HostAccess.Export
    public DomNode<?> getTarget(){
        return target;
    }
}
