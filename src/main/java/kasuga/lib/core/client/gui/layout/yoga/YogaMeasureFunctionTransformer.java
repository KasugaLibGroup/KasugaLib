package kasuga.lib.core.client.gui.layout.yoga;

import org.lwjgl.util.yoga.YGMeasureFunc;
import org.lwjgl.util.yoga.YGMeasureFuncI;

public class YogaMeasureFunctionTransformer {
    public static YGMeasureFuncI transform(YogaMeasureFunction function){
        return YGMeasureFunc.create((YGMeasureFuncI) (nodePointer,width,widthMode,height,heightMode,size)->{
            long returnVal = function.measure(YogaNode.fromPointer(nodePointer),width,YogaMeasureMode.fromInt(widthMode),height,YogaMeasureMode.fromInt(heightMode));
            size.set(YogaMeasureOutput.getWidth(returnVal),YogaMeasureOutput.getHeight(returnVal));
        });
    }
}
