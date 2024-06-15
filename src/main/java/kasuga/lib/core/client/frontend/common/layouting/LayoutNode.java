package kasuga.lib.core.client.frontend.common.layouting;

import kasuga.lib.core.client.frontend.gui.layout.EdgeSize2D;

public interface LayoutNode {
    public void applyChanges();
    public boolean update();
    public void calculate();
    public LayoutBox getPosition();
    public void markDirty();
    public void addChild(int index,LayoutNode node);

    public void removeChild(int index);

    public void removeChild(LayoutNode node);
    public void close();

    EdgeSize2D getBorder();
}
