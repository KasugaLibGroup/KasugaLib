package kasuga.lib.core.client.frontend.dom.registration;

import kasuga.lib.core.javascript.registration.JavascriptPriorityRegistry;
import org.graalvm.polyglot.Value;

public class DOMPriorityRegistry extends JavascriptPriorityRegistry<DOMRegistryItem> {

    @Override
    public DOMRegistryItem fromValue(Value value) {
        value.pin();
        if(value.canExecute()){
           return DOMRegistryItem.fromExecutable(value);
        }
        return DOMRegistryItem.fromConfigurableObject(value);
    }
}
