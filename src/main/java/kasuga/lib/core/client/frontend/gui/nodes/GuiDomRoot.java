package kasuga.lib.core.client.frontend.gui.nodes;

import com.caoccao.javet.annotations.V8Convert;
import kasuga.lib.core.client.frontend.gui.GuiContext;

@V8Convert()
public class GuiDomRoot extends GuiDomNode {
    public GuiDomRoot(GuiContext context) {
        super(context);
        this.styles.decode("height:100%;width:100%");
    }
}
