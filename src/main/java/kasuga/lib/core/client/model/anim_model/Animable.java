package kasuga.lib.core.client.model.anim_model;

import com.mojang.math.Vector3f;

public interface Animable {

    void setOffset(Vector3f position);
    void setAnimRot(Vector3f rotation);
    void setScale(Vector3f scale);
}
