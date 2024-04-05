package kasuga.lib.core.client.animation.infrastructure;

import interpreter.compute.data.functions.Function;
import interpreter.compute.infrastructure.Formula;
import kasuga.lib.core.client.animation.data.Animation;
import kasuga.lib.core.client.render.PoseContext;
import kasuga.lib.core.util.data_type.Pair;

import java.util.ArrayList;

import static kasuga.lib.core.client.animation.data.bones.BoneMovement.cos;
import static kasuga.lib.core.client.animation.data.bones.BoneMovement.sin;

public interface IAnchor {
    record AnchorContext(
            float x, float y, float z, float x_rot, float y_rot, float z_rot, float x_scale, float y_scale, float z_scale
    ){}

    AnchorContext ZERO = new AnchorContext(0, 0, 0, 0, 0, 0, 1, 1, 1);
    AnchorContext getMovement();
    ArrayList<Pair<Formula, PoseContext.ActionType>> getMoves();
    Animation getAnimation();
    IAnchor getParent();
    void updateVars();
    default AnchorContext innerMove
            ()
    {
        float x = 0, y = 0, z = 0, x_rot = 0, y_rot = 0, z_rot = 0, x_scale = 0, y_scale = 0, z_scale = 0;
        if (getParent() != null) {
            IAnchor.AnchorContext context = getParent().getMovement();
            x = context.x(); y = context.y(); z = context.z();
            x_rot = context.x_rot(); y_rot = context.y_rot(); z_rot = context.z_rot();
            x_scale = context.x_scale(); y_scale = context.y_scale(); z_scale = context.z_scale();
        }
        for (Pair<Formula, PoseContext.ActionType> movements : getMoves()) {
            Function function = (Function) movements.getFirst();
            switch (movements.getSecond()) {
                case SCALE -> {
                    if (function.params.size() == 1) {
                        IAnchor anchor = getAnimation().getAsAnchor(function.params.get(0).toString());
                        AnchorContext context = anchor.getMovement();
                        x_scale *= context.x_scale;
                        y_scale *= context.y_scale;
                        z_scale *= context.z_scale;
                    } else {
                        x_scale *= function.params.get(0).getResult();
                        y_scale *= function.params.get(1).getResult();
                        z_scale *= function.params.get(2).getResult();
                    }
                }
                case X_ROT -> {
                    x_rot += function.getCodec().endsWith("rad") ?
                            Math.toDegrees(function.params.get(0).getResult()) :
                            function.params.get(0).getResult();
                }
                case Y_ROT -> {
                    y_rot += function.getCodec().endsWith("rad") ?
                            Math.toDegrees(function.params.get(0).getResult()) :
                            function.params.get(0).getResult();
                }
                case Z_ROT -> {
                    z_rot += function.getCodec().endsWith("rad") ?
                            Math.toDegrees(function.params.get(0).getResult()) :
                            function.params.get(0).getResult();
                }
                case TRANSLATE -> {
                    float px, py, pz;
                    if (function.params.size() == 1) {
                        IAnchor anchor = getAnimation().getAsAnchor(function.params.get(0).toString());
                        AnchorContext context = anchor.getMovement();
                        px = context.x * x_scale; py = context.y * y_scale; pz = context.z * z_scale;
                    } else {
                        px = function.params.get(0).getResult() * x_scale;
                        py = function.params.get(1).getResult() * y_scale;
                        pz = function.params.get(2).getResult() * z_scale;
                    }
                    x += px * cos(y_rot) * cos(z_rot) +
                            py * (sin(x_rot) * sin(y_rot) * cos(z_rot) - cos(x_rot) * sin(z_rot)) +
                            pz * (sin(y_rot) * cos(x_rot) * cos(z_rot) + sin(x_rot) * sin(z_rot));
                    y += px * cos(y_rot) * sin(z_rot) +
                            py * (sin(x_rot) * sin(y_rot) * sin(z_rot) + cos(x_rot) * cos(z_rot)) +
                            pz * (sin(y_rot) * sin(z_rot) * cos(x_rot) - sin(x_rot) * cos(z_rot));
                    z += px * -sin(y_rot) + py * sin(x_rot) * cos(y_rot) + pz * cos(x_rot) * cos(y_rot);
                }
            }
        }
        return new AnchorContext(x, y, z, x_rot, y_rot, z_rot, x_scale, y_scale, z_scale);
    }

    interface AnchorInvoker {
        void invoke();
    }
}
