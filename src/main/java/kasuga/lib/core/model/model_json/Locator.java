package kasuga.lib.core.model.model_json;

import com.mojang.math.Vector3f;

public class Locator {
    public final Vector3f position, rotation;

    public Locator(Vector3f position, Vector3f rotation) {
        this.rotation = rotation;
        this.position = position;
    }

    public Locator offset(Vector3f offset) {
        Vector3f neoPos = position.copy();
        neoPos.add(offset);
        return new Locator(neoPos, rotation);
    }
}
