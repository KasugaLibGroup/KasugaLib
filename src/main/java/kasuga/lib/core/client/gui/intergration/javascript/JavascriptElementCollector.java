package kasuga.lib.core.client.gui.intergration.javascript;

import kasuga.lib.core.client.gui.components.Node;
import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class JavascriptElementCollector {
    JavascriptElementCollector(){
    }

    HashMap<JavascriptGuiElement, AtomicInteger> referenceCounter = new HashMap<>();

    public void collect(JavascriptGuiElement guiElement) {
        this.referenceCounter.put(guiElement,new AtomicInteger());
    }

    public void close() {
        for (JavascriptGuiElement guiElement : this.referenceCounter.keySet()) {
            guiElement.close();
        }
    }
}
