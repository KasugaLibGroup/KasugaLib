package kasuga.lib.core.client.frontend.dom.registration;

import kasuga.lib.core.client.frontend.dom.DomContext;
import kasuga.lib.core.javascript.engine.JavascriptValue;

public abstract class DOMRegistryItem {
    public String renderEngine = "yoga";

    public String lightLevel = "full";

    abstract JavascriptValue render(DomContext<?,?> document);


    public static DOMRegistryItem fromExecutable(JavascriptValue executable){
        JavascriptValue finalExecutable = executable.cloneValue();
        return new DOMRegistryItem() {
            @Override
            JavascriptValue render(DomContext<?, ?> document) {
                return finalExecutable.execute(document);
            }
        };
    }

    public static DOMRegistryItem fromConfigurableObject(JavascriptValue object){
        JavascriptValue finalObject = object.cloneValue();

        if(!finalObject.hasMember("render") || !finalObject.getMember("render").canExecute()){
            throw new IllegalArgumentException("Object must have a render method");
        }

        DOMRegistryItem item = new DOMRegistryItem() {
            @Override
            JavascriptValue render(DomContext<?, ?> document) {
                return finalObject.invokeMember("render", document);
            }
        };

        if(finalObject.hasMember("renderEngine")){
            item.renderEngine = object.getMember("renderEngine").asString();
        }

        if(finalObject.hasMember("lightLevel")){
            item.lightLevel = object.getMember("lightLevel").asString();
        }

        return item;
    }
}
