package kasuga.lib.core.client.frontend.dom.registration;

import kasuga.lib.core.client.frontend.dom.DomContext;
import org.graalvm.polyglot.Value;

@FunctionalInterface
public interface DOMRegistryItem {
    Value render(DomContext<?,?> document);
}
