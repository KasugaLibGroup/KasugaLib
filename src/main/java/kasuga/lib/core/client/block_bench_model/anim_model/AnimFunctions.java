package kasuga.lib.core.client.block_bench_model.anim_model;

import interpreter.compute.data.Namespace;
import interpreter.compute.data.functions.Function;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimFunctions {

    public static final Namespace animRoot = new Namespace();

    public static final Function COS = animRoot.register1Param("math.cos",
            (input) -> (float) Math.cos(Math.toRadians(input)));

    public static final Function SIN = animRoot.register1Param("math.sin",
            (input) -> (float) Math.sin(Math.toRadians(input)));

    public static final Function TAN = animRoot.register1Param("math.tan",
            (input) -> (float) Math.tan(Math.toRadians(input)));

    public static final Function SQRT = animRoot.register1Param("math.sqrt",
            (input) -> (float) Math.sqrt(input));

    public static final Function ASIN = animRoot.register1Param("math.asin",
            (input) -> (float) Math.toDegrees(Math.asin(input)));

    public static final Function ACOS = animRoot.register1Param("math.acos",
            (input) -> (float) Math.toDegrees(Math.acos(input)));

    public static final Function ATAN = animRoot.register1Param("math.atan",
            (input) -> (float) Math.toDegrees(Math.atan(input)));

    public static final Function ABS = animRoot.register1Param("math.abs",
            Math::abs);

    public static final Function SGN = animRoot.register1Param("math.sgn",
            Math::signum);

    public static final Function POW = animRoot.register2Param("math.pow",
            (input1, input2) -> (float) Math.pow(input1, input2));

    public static final Function MAX = animRoot.register2Param("math.max",
            Math::max);

    public static final Function MIN = animRoot.register2Param("math.min",
            Math::min);

    public void invoke(){}
}
