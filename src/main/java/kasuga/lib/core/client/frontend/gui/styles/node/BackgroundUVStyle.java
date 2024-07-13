package kasuga.lib.core.client.frontend.gui.styles.node;

import kasuga.lib.core.client.frontend.common.layouting.LayoutBoxI;
import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class BackgroundUVStyle extends Style<LayoutBoxI, StyleTarget> {

    public static final StyleType<BackgroundUVStyle, StyleTarget> TYPE = SimpleNodeStyleType.of(BackgroundUVStyle::new, "0 0 0 0");
    public static final BackgroundUVStyle EMPTY = new BackgroundUVStyle();
    String uvString = "";
    boolean valid = false;
    LayoutBoxI box;

    public BackgroundUVStyle(String uv){
        int[] positions = new int[4];
        int pointer = 0;
        uvString = uv;
        try(Scanner scanner=new Scanner(uv)){
            while(scanner.hasNext() && pointer < 4) {
                positions[pointer] = scanner.nextInt();
                pointer++;
            }
        }catch (InputMismatchException mismatchException){
            return;
        }
        if(pointer != 4)
            return;
        box = LayoutBoxI.of(positions[0], positions[1], positions[2], positions[3]);
        valid = true;
    }

    public BackgroundUVStyle() {}
    @Override
    public boolean isValid(Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>> origin) {
        return valid;
    }

    @Override
    public StyleType<?, StyleTarget> getType() {
        return TYPE;
    }

    @Override
    public StyleTarget getTarget() {
        return StyleTarget.GUI_DOM_NODE.create((node)->{
            node.getBackgroundRenderer().setUV(box.x, box.y, box.width, box.height);
        });
    }

    @Override
    public String getValueString() {
        return uvString;
    }

    @Override
    public LayoutBoxI getValue() {
        return box;
    }
}
