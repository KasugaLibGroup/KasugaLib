package kasuga.lib.core.client.frontend.common.style;

import java.util.Map;

public abstract class Style<P,R> {

    public abstract boolean isValid(Map<StyleType<?,R>, Style<?,R>> origin);

    public abstract StyleType<?, R> getType();

    public abstract R getTarget();

    public abstract String getValueString();

    public abstract P getValue();

    public String toString(){
        return super.toString();
    }
}
