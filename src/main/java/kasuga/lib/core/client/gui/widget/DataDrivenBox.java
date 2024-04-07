package kasuga.lib.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.gui.SimpleWidget;
import kasuga.lib.core.client.gui.enums.LocationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataDrivenBox extends DataDrivenWidget implements IBoxWidget {
    private final ArrayList<SimpleWidget> widgets;
    public DataDrivenBox(int pX, int pY, int pWidth, int pHeight, LocationType type) {
        super(pX, pY, pWidth, pHeight, type);
        widgets = new ArrayList<>();
    }

    @Override
    public List<SimpleWidget> getChildren() {
        return widgets;
    }

    @Override
    public SimpleWidget getChild(int index) {
        return widgets.get(index);
    }

    @Override
    public void addChild(SimpleWidget widget) {
        widgets.add(widget);
    }

    @Override
    public void updateChildIndex(int oldIndex, int newIndex) {
        widgets.set(newIndex, widgets.get(oldIndex));
    }

    @Override
    public void setChild(int index, SimpleWidget widget) {
        widgets.set(index, widget);
    }

    @Override
    public int childCount() {
        return widgets.size();
    }

    @Override
    public boolean hasChild() {
        return !widgets.isEmpty();
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        for (SimpleWidget widget : widgets) widget.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void onClose() {
        super.onClose();
        for (SimpleWidget widget : widgets) widget.onClose();
    }
}
