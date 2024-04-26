package kasuga.lib.core.client.gui.intergration.javascript;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import kasuga.lib.core.client.gui.components.ComponentRegistry;
import kasuga.lib.core.client.gui.components.ComponentType;
import kasuga.lib.core.client.gui.components.GuiComponent;
import kasuga.lib.core.client.gui.components.Node;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;

public class JavascriptGuiContainer {
    private final JavascriptElementCollector collector = new JavascriptElementCollector();

    private final Node root;

    public JavascriptGuiContainer(Node root) {
        this.root = root;
    }

    @HostAccess.Export
    public JavascriptGuiElement createElement(Value type,Value propsJson){
        if(!type.isString() || !(propsJson.isString() || propsJson.isNull())){
            return null;
        }
        return createElement(type.asString(),propsJson.isString() ? propsJson.asString() : null);
    }
    public JavascriptGuiElement createElement(String type,@Nullable String propsJson){
        JsonObject props = new JsonObject();
        try{
            if(propsJson != null && !Objects.equals(propsJson, "null")){
                JsonElement element = JsonParser.parseString(propsJson);
                if(!element.isJsonObject()){
                    return null;
                }
                props = element.getAsJsonObject();
            }
        }catch (JsonSyntaxException e){
            return null;
        }
        ComponentType<?> componentType = ComponentRegistry.getComponent(type);
        Node component = componentType.create(props);
        JavascriptGuiElement guiElement = new JavascriptGuiElement(component);
        this.collector.collect(guiElement);
        return guiElement;
    }

    @HostAccess.Export
    public void appendElement(Value value){
        if(!value.isHostObject())
            return;
        Object object = value.asHostObject();
        if(!(object instanceof JavascriptGuiElement element))
            return;
        appendElement(element);
    }

    public void appendElement(JavascriptGuiElement element){
        root.addChild(element.getNode());
    }

    @HostAccess.Export
    public void removeElement(Value value){
        if(!value.isHostObject())
            return;
        Object object = value.asHostObject();
        if(!(object instanceof JavascriptGuiElement element))
            return;
        removeElement(element);
    }

    public void removeElement(JavascriptGuiElement element){
        root.removeChild(element.getNode());
    }

    public Node getRoot() {
        return root;
    }

    public void close() {
        this.collector.close();
        this.root.children().clear();
    }
}
