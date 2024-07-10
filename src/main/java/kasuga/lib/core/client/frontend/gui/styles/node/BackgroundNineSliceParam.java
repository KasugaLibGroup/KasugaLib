package kasuga.lib.core.client.frontend.gui.styles.node;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class BackgroundNineSliceParam extends Style<float[], StyleTarget> {

    public static final StyleType<BackgroundNineSliceParam, StyleTarget> TYPE = SimpleNodeStyleType.of(BackgroundNineSliceParam::new, "0 0");
    public static final BackgroundNineSliceParam EMPTY = new BackgroundNineSliceParam();
    String uvString = "";
    boolean valid = false;
    float borderSize, borderScale;

    float[] value = new float[2];

    public BackgroundNineSliceParam(String uv){
        float[] positions = new float[2];
        int pointer = 0;
        uvString = uv;
        try(Scanner scanner=new Scanner(uv)){
            while(scanner.hasNext() && pointer < 2) {
                positions[pointer] = scanner.nextFloat();
                pointer++;
            }
        }catch (InputMismatchException mismatchException){
            return;
        }
        if(pointer != 2)
            return;
        borderSize = positions[0];
        borderScale = positions[1];
        value = positions;
        valid = true;
    }

    public BackgroundNineSliceParam() {}
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
            node.getBackgroundRenderer().setNineSlicedParam(borderSize, borderScale);
        });
    }

    @Override
    public String getValueString() {
        return uvString;
    }

    @Override
    public float[] getValue() {
        return value;
    }
}
