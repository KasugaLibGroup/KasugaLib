package kasuga.lib.core.client.frontend.common.event;

import kasuga.lib.core.client.frontend.dom.nodes.DomNode;
import org.graalvm.polyglot.HostAccess;

public abstract class Event {
    protected final DomNode<?> currentTarget;
    protected final DomNode<?> target;
    protected final boolean trusted;
    protected boolean defaultPrevented;
    protected boolean propagationStopped;

    public Event(DomNode<?> currentTarget, DomNode<?> target, boolean trusted){
        this.currentTarget = currentTarget;
        this.target = target;
        this.trusted = trusted;
    }

    @HostAccess.Export
    public boolean isBubbles(){
        return false;
    }

    @HostAccess.Export
    public boolean isCancelable(){
        return true;
    }

    @HostAccess.Export
    public DomNode<?> getCurrentTarget(){
        return currentTarget;
    }

    @HostAccess.Export
    public boolean getDefaultPrevented(){
        return this.defaultPrevented;
    }

    @HostAccess.Export
    public boolean isTrusted(){
        return trusted;
    }

    @HostAccess.Export
    public DomNode<?> getTarget(){
        return target;
    }

    @HostAccess.Export
    public abstract String getType();

    @HostAccess.Export
    public void stopPropagation(){
        this.propagationStopped = true;
    }

    @HostAccess.Export
    public boolean isPropagationStopped(){
        return propagationStopped;
    }

    @HostAccess.Export
    public void preventDefault(){
        this.defaultPrevented = true;
    }
}
