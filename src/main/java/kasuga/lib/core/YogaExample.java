package kasuga.lib.core;

import kasuga.lib.core.client.gui.layout.yoga.YogaMeasureFunction;
import kasuga.lib.core.client.gui.layout.yoga.YogaMeasureOutput;
import kasuga.lib.core.client.gui.layout.yoga.YogaNode;

public class YogaExample {
    public static void example(){

        YogaNode node = YogaNode.create();
        node.setWidth(100);
        node.setHeight(100);

        YogaNode children = YogaNode.create();
        children.setMaxWidth(10);
        children.setMaxHeight(10);
        children.setMeasureFunction((YogaMeasureFunction) (yogaNode,
                                                           width,
                                                           widthMode,
                                                           height,
                                                           heightMode)->{
            System.out.println("Measure!");
            return YogaMeasureOutput.make(5,5);
        });
        // node.setAlignContent(YogaAlign.CENTER);
        node.addChildAt(0,children);
        node.calculateLayout(100,100);
        System.out.printf("Yoga layout result:\nChild X:%.2f\nChild Y:%.2f\nChild Width:%.2f\nChild Height:%.2f\n",children.getLayoutLeft(),children.getLayoutTop(),children.getLayoutWidth(),children.getLayoutHeight());
    }
}
