package kasuga.lib.core.client.frontend.common.event;

import kasuga.lib.core.javascript.engine.annotations.HostAccess;

public abstract class Event {

    protected final boolean trusted;
    protected boolean defaultPrevented;

    public Event(boolean trusted){
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
    public boolean getDefaultPrevented(){
        return this.defaultPrevented;
    }

    @HostAccess.Export
    public boolean isTrusted(){
        return trusted;
    }


    @HostAccess.Export
    public abstract String getType();


    @HostAccess.Export
    public void preventDefault(){
        this.defaultPrevented = true;
    }
}
