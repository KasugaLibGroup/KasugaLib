package kasuga.lib.core.model;

import com.mojang.math.Vector3f;

import java.util.List;

public interface Rotationable {
    void applyRotation(List<RotationInstruction> instructions);

    record RotationInstruction(Vector3f pivot, Vector3f rotation) {}

}
