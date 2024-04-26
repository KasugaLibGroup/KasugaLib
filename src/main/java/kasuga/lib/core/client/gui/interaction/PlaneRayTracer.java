package kasuga.lib.core.client.gui.interaction;

import com.mojang.math.Vector3f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class PlaneRayTracer {
    Entity player;

    Vector3f planeOrigin;

    Vector3f planeNormal;

    int height;

    int width;

    public PlaneRayTracer() {
    }

    public Vector3f trace(){
        if(this.player == null)
            return null;
        Vector3f eyePosition = vec3ToVector3f(player.getEyePosition());
        Vector3f viewVector = vec3ToVector3f(player.getViewVector(1));
        Vector3f planeOriginPoint = new Vector3f(planeOrigin.x(),planeOrigin.y(),planeOrigin.z());
        Vector3f planeNormalVector = new Vector3f(planeNormal.x(),planeNormal.y(),planeNormal.z());
        planeOriginPoint.sub(eyePosition);
        viewVector.normalize();
        float d = planeOriginPoint.dot(planeNormalVector) / viewVector.dot(planeNormalVector);
        viewVector.mul(d);
        viewVector.add(eyePosition);
        return viewVector;
    }

    public static Vector3f vec3ToVector3f(Vec3 i){
        return new Vector3f((float) i.x,(float) i.y,(float) i.z);
    }

    public void setPlanePose(Vector3f planeOrigin,Vector3f plainNormal) {
        this.planeNormal = plainNormal;
        this.planeOrigin = planeOrigin;
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }
}
