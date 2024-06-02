package kasuga.lib.core.client.frontend.gui.nodes;

import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.gui.SourceInfo;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;

import java.util.HashMap;

public class GuiDomRoot extends GuiDomNode {
    LayoutEngine<?> layoutEngine = LayoutEngines.YOGA;

    HashMap<Object,SourceInfo> info = new HashMap<>();

    public SourceInfo getSourceInfo(Object source) {
        return info.get(source);
    }

    public void layout(Object source){
        this.layoutManager.executeLayout(source,layoutEngine);
    }

    public void setLayoutEngine(LayoutEngine<?> layoutEngine) {
        this.layoutEngine = layoutEngine;
    }

    public void setSourceInfo(Object source,SourceInfo sourceInfo) {
        this.info.put(source, sourceInfo);
    }
}
