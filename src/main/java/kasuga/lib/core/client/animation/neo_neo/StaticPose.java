package kasuga.lib.core.client.animation.neo_neo;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.animation.neo_neo.point.Point;
import kasuga.lib.core.client.animation.neo_neo.scaling.Scaling;
import kasuga.lib.core.client.render.model.MultiPartModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

public class StaticPose {
    private ResourceLocation model;
    private final HashMap<String, Point> holder;

    public StaticPose(ResourceLocation model) {
        this.model = model;
        this.holder = new HashMap<>();
    }

    public void addPose(String bone, Point pose) {
        holder.put(bone, pose);
    }

    public boolean containsPose(String bone) {
        return holder.containsKey(bone);
    }

    public boolean removePose(String bone) {
        if (!containsPose(bone)) return false;
        holder.remove(bone);
        return true;
    }

    public Point getPose(String bone) {
        return holder.getOrDefault(bone, null);
    }

    public ResourceLocation getModel() {
        return model;
    }

    public HashMap<String, Point> getHolder() {
        return holder;
    }

    public void translate(String bone, Vec3 translation) {
        Point neo = this.holder.getOrDefault(bone, new Point(Vec3.ZERO, Vec3.ZERO, Scaling.STARTER, true));
        neo.translate(translation);
        this.holder.put(bone, neo);
    }

    public void absTranslate(String bone, Vec3 translation) {
        Point neo = this.holder.getOrDefault(bone, new Point(Vec3.ZERO, Vec3.ZERO, Scaling.STARTER, true));
        neo.absTranslate(translation);
        this.holder.put(bone, neo);
    }

    public void rotate(String bone, Vec3 rotation, boolean degree) {
        Point neo = this.holder.getOrDefault(bone, new Point(Vec3.ZERO, Vec3.ZERO, Scaling.STARTER, true));
        neo.rotate(rotation, degree);
        this.holder.put(bone, neo);
    }

    public void scale(String bone, Vec3 scaling) {
        Point neo = this.holder.getOrDefault(bone, new Point(Vec3.ZERO, Vec3.ZERO, Scaling.STARTER, true));
        neo.scale(scaling);
        this.holder.put(bone, neo);
    }

    public void setScale(String bone, Vec3 scaling) {
        Point neo = this.holder.getOrDefault(bone, new Point(Vec3.ZERO, Vec3.ZERO, Scaling.STARTER, true));
        neo.setScale(scaling);
        this.holder.put(bone, neo);
    }
}
