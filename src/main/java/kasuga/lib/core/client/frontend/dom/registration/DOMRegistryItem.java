package kasuga.lib.core.client.frontend.dom.registration;

import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.javascript.engine.JavascriptValue;

public abstract class DOMRegistryItem {
    public String renderEngine = "yoga";

    public String lightLevel = "full";

    abstract JavascriptValue render(DomContext<?,?> document);


    public static DOMRegistryItem fromExecutable(JavascriptValue executable){
        return new DOMRegistryItem() {
            @Override
            JavascriptValue render(DomContext<?, ?> document) {
                return executable.execute(document);
            }
        };
    }

    public static DOMRegistryItem fromConfigurableObject(JavascriptValue object){

        if(!object.hasMember("render") || !object.getMember("render").canExecute()){
            throw new IllegalArgumentException("Object must have a render method");
        }

        DOMRegistryItem item = new DOMRegistryItem() {
            @Override
            JavascriptValue render(DomContext<?, ?> document) {
                return object.invokeMember("render", document);
            }
        };

        if(object.hasMember("renderEngine")){
            item.renderEngine = object.getMember("renderEngine").asString();
        }

        if(object.hasMember("lightLevel")){
            item.lightLevel = object.getMember("lightLevel").asString();
        }
        return item;
    }
}
