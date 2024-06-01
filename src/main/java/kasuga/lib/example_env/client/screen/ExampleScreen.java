package kasuga.lib.example_env.client.screen;

import kasuga.lib.core.client.gui.KasugaScreen;
import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.components.Text;
import kasuga.lib.core.client.gui.components.View;
import kasuga.lib.core.client.gui.layout.yoga.YogaAlign;
import kasuga.lib.core.client.gui.layout.yoga.YogaFlexDirection;
import kasuga.lib.core.client.gui.layout.yoga.YogaNodeType;
import kasuga.lib.core.client.gui.style.Style;
import kasuga.lib.core.client.gui.style.Styles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Supplier;

public class ExampleScreen {
//    public static Supplier<Screen> supplier(){
//        return ()->{
//            return KasugaScreen.screen(
//                    ((Supplier<Node>)()->{
//                        View view1 = new View();
//                        view1.style().addStyle(Styles.TOP.create("10"));
//                        view1.style().addStyle(Styles.LEFT.create("10"));
//                        view1.style().addStyle(Styles.WIDTH.create("50%"));
//                        view1.style().addStyle(Styles.HEIGHT.create("50%"));
//                        view1.getLocatorNode().setAlignContent(YogaAlign.FLEX_START);
//                        view1.getLocatorNode().setFlexDirection(YogaFlexDirection.ROW);
//                        View child1 = new View();
//                        child1.style().addStyle(Styles.WIDTH.create("50"));
//                        child1.style().addStyle(Styles.HEIGHT.create("50"));
//                        view1.addChild(child1);
//                        View child2 = new View();
//                        view1.addChild(child2);
//                        child2.style().addStyle(Styles.WIDTH.create("50"));
//                        child2.style().addStyle(Styles.HEIGHT.create("50"));
//
//
//                        View child3 = new View();
//                        view1.addChild(child3);
//                        child3.style().addStyle(Styles.WIDTH.create("50"));
//                        child3.style().addStyle(Styles.HEIGHT.create("50"));
//
//                        Text text1 = new Text();
//                        text1.getLocatorNode().setNodeType(YogaNodeType.TEXT);
//                        text1.setContent("Hello,world");
//                        view1.addChild(text1);
//
//                        Text text2 = new Text();
//                        text2.getLocatorNode().setNodeType(YogaNodeType.TEXT);
//                        text2.setContent("I'm TimeBather");
//                        view1.addChild(text2);
//                        return view1;
//                    }).get()
//            );
//        };
//    }
}
