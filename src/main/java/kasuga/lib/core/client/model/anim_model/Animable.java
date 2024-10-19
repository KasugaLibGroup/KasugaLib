package kasuga.lib.core.client.model.anim_model;

import org.joml.Vector3f;

public interface Animable {

    void setOffset(Vector3f position);
    void setAnimRot(Vector3f rotation);
    void setScale(Vector3f scale);
}
