package kasuga.lib.core.client.gui.intergration.javascript;

import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.components.Text;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.util.ArrayList;
import java.util.List;

public class JavascriptGuiElement {
    final Node component;

    public List<JavascriptGuiElement> children = new ArrayList<>();
    JavascriptGuiElement(Node component){
        this.component = component;
    }

    @HostAccess.Export
    @HostAccess.DisableMethodScoping
    public void setElementContent(Value value){
        if(!value.isString())
            return;
        setElementContent(value.asString());
    }
    public void setElementContent(String content){
        if(this.component instanceof Text){
            ((Text) this.component).setContent(content);
        }
    }

    @HostAccess.Export
    public void addChild(Value child){
        if(child.isHostObject() && child.asHostObject() instanceof JavascriptGuiElement element)
            addChild(element);
    }

    public void addChild(JavascriptGuiElement element){
        this.getNode().addChild(element.getNode());
        children.add(element);
    }


    @HostAccess.Export
    public void removeChild(Value child){
        if(child.isHostObject() && child.asHostObject() instanceof JavascriptGuiElement element)
            removeChild(element);
    }

    public void removeChild(JavascriptGuiElement element){
        this.getNode().removeChild(element.getNode());
        children.remove(element);
    }

    public Node getNode(){
        return component;
    }

    public void close() {
        this.component.close();
    }

    @HostAccess.Export
    public void listenMouse(Value name,Value listenHandler){
        if(!name.isString() || !listenHandler.canExecute())
            return;
        listenHandler.pin();
        this.component.listenMouseEvent(name.asString(),(event)->{
            Value returnVal = listenHandler.execute(event);
            if(!returnVal.isBoolean())
                return false;
            return returnVal.asBoolean();
        });
    }
}
