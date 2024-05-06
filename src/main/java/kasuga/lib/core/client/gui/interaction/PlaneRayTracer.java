package kasuga.lib.core.client.gui.interaction;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class PlaneRayTracer {
    Entity player;

    Vector3f planeOrigin;

    Vector3f planeNormal;

    int height;

    int width;
    private Matrix4f invMatrix;

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

    public Vec2 tracePlane(){
        Vector3f traceResult = trace();
        Vector4f planePoint = new Vector4f(traceResult.x(),traceResult.y(),traceResult.z(),1);
        planePoint.transform(invMatrix);
        return new Vec2(planePoint.x() * planePoint.w(),planePoint.y() * planePoint.w());
    }

    public static Vector3f vec3ToVector3f(Vec3 i){
        return new Vector3f((float) i.x,(float) i.y,(float) i.z);
    }

    public void setPlanePose(PoseStack.Pose pose) {
        this.planeNormal = new Vector3f(0,0,1);
        planeNormal.transform(pose.normal());
        Vector4f origin4f = new Vector4f(0,0,0,1);
        origin4f.transform(pose.pose());
        this.planeOrigin = new Vector3f(origin4f.x() * origin4f.w() , origin4f.y() * origin4f.w() , origin4f.z() * origin4f.w());
        Matrix4f inverse = pose.pose().copy();
        inverse.invert();
        this.invMatrix = inverse;
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }
}
