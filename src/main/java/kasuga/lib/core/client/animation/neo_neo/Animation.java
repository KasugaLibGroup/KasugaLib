package kasuga.lib.core.client.animation.neo_neo;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.animation.neo_neo.key_frame.KeyFrame;
import kasuga.lib.core.client.animation.neo_neo.key_frame.KeyFrameHolder;
import kasuga.lib.core.client.animation.neo_neo.point.Point;
import kasuga.lib.core.client.animation.neo_neo.rotation.BezierRotation;
import kasuga.lib.core.client.animation.neo_neo.rotation.CustomRotation;
import kasuga.lib.core.client.animation.neo_neo.rotation.LinearRotation;
import kasuga.lib.core.client.animation.neo_neo.rotation.Rotation;
import kasuga.lib.core.client.animation.neo_neo.scaling.BezierScaling;
import kasuga.lib.core.client.animation.neo_neo.scaling.CustomScaling;
import kasuga.lib.core.client.animation.neo_neo.scaling.LinearScaling;
import kasuga.lib.core.client.animation.neo_neo.scaling.Scaling;
import kasuga.lib.core.client.animation.neo_neo.translation.BezierTranslation;
import kasuga.lib.core.client.animation.neo_neo.translation.CustomTranslation;
import kasuga.lib.core.client.animation.neo_neo.translation.LinearTranslation;
import kasuga.lib.core.client.animation.neo_neo.translation.Translation;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.registrations.client.ModelReg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class Animation {
    public static final KeyFrameHolder<Translation> TRANSLATIONS = new KeyFrameHolder<Translation>(
            Pair.of(new ResourceLocation(KasugaLib.MOD_ID, "linear_translation"), new KeyFrame<LinearTranslation>(
                    ((current, next) -> new LinearTranslation(next.getData().subtract(current.getData()), current.getTime(), next.getTime()))
            )),
            Pair.of(new ResourceLocation(KasugaLib.MOD_ID, "bezier_translation"), new KeyFrame<BezierTranslation>(
                    ((current, next) -> new BezierTranslation(next.getData().subtract(current.getData()), current.getTime(), next.getTime()))
            )),
            Pair.of(new ResourceLocation(KasugaLib.MOD_ID, "custom_translation"), new KeyFrame<CustomTranslation>(
                    (((current, next) -> new CustomTranslation(current.getTime(), next.getTime())))
            ))
    );

    public static final KeyFrameHolder<Rotation> ROTATIONS = new KeyFrameHolder<Rotation>(
            Pair.of(new ResourceLocation(KasugaLib.MOD_ID, "linear_rotation"), new KeyFrame<LinearRotation>(
                    (current, next) -> new LinearRotation(next.getData().subtract(current.getData()), current.getTime(), next.getTime(), false)
            )),
            Pair.of(new ResourceLocation(KasugaLib.MOD_ID, "bezier_rotation"), new KeyFrame<BezierRotation>(
                    (current, next) -> new BezierRotation(next.getData().subtract(current.getData()), current.getTime(), next.getTime(), false)
            )),
            Pair.of(new ResourceLocation(KasugaLib.MOD_ID, "custom_rotation"), new KeyFrame<CustomRotation>(
                    (current, next) -> new CustomRotation(current.getTime(), next.getTime(), false)
            ))
    );

    public static final KeyFrameHolder<Scaling> SCALINGS = new KeyFrameHolder<Scaling>(
            Pair.of(new ResourceLocation(KasugaLib.MOD_ID, "linear_scaling"), new KeyFrame<LinearScaling>(
                    (current, next) -> new LinearScaling(next.getData().subtract(current.getData()), current.getTime(), next.getTime())
            )),
            Pair.of(new ResourceLocation(KasugaLib.MOD_ID, "bezier_scaling"), new KeyFrame<BezierScaling>(
                    (current, next) -> new BezierScaling(next.getData().subtract(current.getData()), current.getTime(), next.getTime())
            )),
            Pair.of(new ResourceLocation(KasugaLib.MOD_ID, "custom_scaling"), new KeyFrame<CustomScaling>(
                    (current, next) -> new CustomScaling(current.getTime(), next.getTime())
            ))
    );

    private final StaticPose pose;
    private final ResourceLocation model;

    public String name;
    private final StateHolder<Translation> translationState;
    private final StateHolder<Rotation> rotationState;
    private final StateHolder<Scaling> scalingState;
    public Animation(ModelReg registration) {
        this(registration, new StaticPose(registration.location()));
    }

    public Animation(ResourceLocation resourceLocation) {
        this.model = resourceLocation;
        this.pose = new StaticPose(resourceLocation);
        this.translationState = new StateHolder<>(TRANSLATIONS, "translation");
        this.rotationState = new StateHolder<>(ROTATIONS, "rotation");
        this.scalingState = new StateHolder<>(SCALINGS, "scaling");
    }

    public Animation(ModelReg reg, StaticPose pose) {
        this.model = reg.location();
        this.pose = pose;
        this.translationState = new StateHolder<>(TRANSLATIONS, "translation");
        this.rotationState = new StateHolder<>(ROTATIONS, "rotation");
        this.scalingState = new StateHolder<>(SCALINGS, "scaling");
    }

    public ResourceLocation getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public StaticPose getPose() {
        return pose;
    }

    public void addPose(String bone, Point pose) {
        this.pose.addPose(bone, pose);
    }

    public void translatePose(String bone, Vec3 translation) {
        this.pose.translate(bone, translation);
    }

    public void absTranslatePose(String bone, Vec3 translation) {
        this.pose.absTranslate(bone, translation);
    }

    public void rotatePose(String bone, Vec3 rotation, boolean degree) {
        this.pose.rotate(bone, rotation, degree);
    }

    public void scalePose(String bone, Vec3 scale) {
        this.pose.setScale(bone, scale);
    }

    public Point getPose(String bone) {
        return pose.getPose(bone);
    }

    public boolean containsPose(String bone) {
        return pose.containsPose(bone);
    }

    public boolean removePose(String bone) {
        return pose.removePose(bone);
    }

    public void setName(String name) {
        this.name = name;
    }

    public StateHolder<Translation> getTranslation() {
        return translationState;
    }

    public StateHolder<Rotation> getRotation() {
        return rotationState;
    }

    public StateHolder<Scaling> getScaling() {
        return scalingState;
    }

    public void addKeyFrame(Float time, String bone, String side, ResourceLocation type) {
        switch (side) {
            case "translation" -> translationState.addKeyFrame(bone, time, type);
            case "rotation" -> rotationState.addKeyFrame(bone, time, type);
            case "scaling" -> scalingState.addKeyFrame(bone, time, type);
        }
    }

    public void addTranslationKeyFrame(Float time, String bone, ResourceLocation type) {
        translationState.addKeyFrame(bone, time, type);
    }

    public void addRotationKeyFrame(Float time, String bone, ResourceLocation type) {
        rotationState.addKeyFrame(bone, time, type);
    }

    public void addScalingKeyFrame(Float time, String bone, ResourceLocation type) {
        scalingState.addKeyFrame(bone, time, type);
    }
}
