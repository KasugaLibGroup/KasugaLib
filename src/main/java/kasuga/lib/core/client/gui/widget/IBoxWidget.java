package kasuga.lib.core.client.gui.widget;

import kasuga.lib.core.client.gui.SimpleWidget;

import java.util.HashMap;
import java.util.List;

public interface IBoxWidget {
    List<SimpleWidget> getChildren();
    SimpleWidget getChild(int index);
    void addChild(SimpleWidget widget);
    void updateChildIndex(int oldIndex, int newIndex);
    void setChild(int index, SimpleWidget widget);
    int childCount();
    boolean hasChild();
}