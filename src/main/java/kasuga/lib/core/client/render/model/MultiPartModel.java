package kasuga.lib.core.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import kasuga.lib.registrations.client.ModelReg;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiPartModel extends SimpleModel {
    private final HashMap<String, SimpleModel> bones;
    public StaticMovements staticMovements;

    public MultiPartModel(@NotNull String key) {
        super(key, null);
        bones = new HashMap<>();
    }

    public MultiPartModel(@NotNull String key, HashMap<String, SimpleModel> bones) {
        super(key, null);
        this.bones = (HashMap<String, SimpleModel>) Map.copyOf(bones);
    }

    public MultiPartModel(@NotNull ModelReg reg) {
        this(reg.registrationKey);
    }

    public void copyModelFrom(SimpleModel model) {
        if(model instanceof MultiPartModel multiPartModel)
            this.bones.putAll(multiPartModel.bones);
        else
            this.bones.put(model.key, model);
    }

    public void addBoneByPath(String path, SimpleModel bone) {
        boolean flag = path.lastIndexOf(".") == -1;
        if(flag) {
            addBone(bone);
        } else {
            String p = path.substring(0, path.lastIndexOf("."));
            MultiPartModel multiPartModel = getOrCreate(p);
            multiPartModel.addBone(bone);
        }
    }

    public MultiPartModel getOrCreate(String path) {
        return getOrCreate(path.split("\\."), 0);
    }

    private MultiPartModel getOrCreate(String[] paths, int index) {
        if(index < paths.length && index > -1) {
            boolean flag = containsBone(paths[index]);
            SimpleModel model = flag ? getBone(paths[index]) : null;
            if(!(model instanceof MultiPartModel)) {
                MultiPartModel subModel = new MultiPartModel(paths[index]);
                if(flag) {
                    subModel.addBone(model);
                    this.bones.remove(paths[index]);
                }
                addBone(subModel);
                if(index < paths.length - 1)
                    return subModel.getOrCreate(paths, index+1);
                else
                    return subModel;
            } else {
                if(index < paths.length - 1)
                    return ((MultiPartModel) model).getOrCreate(paths, index+1);
                return (MultiPartModel) model;
            }
        } else {return null;}
    }

    public @Nullable SimpleModel getBoneByPath(String path) {
        if(path.lastIndexOf(".") == -1) {
            return containsBone(path) ? getBone(path) : null;
        }
        return getBoneByPath(path.split("\\."), 0);
    }

    private SimpleModel getBoneByPath(String[] paths, int index) {
        if(index < paths.length && index > -1) {
            boolean flag = containsBone(paths[index]);
            SimpleModel model = flag ? getBone(paths[index]) : null;
            if(!(model instanceof MultiPartModel)) {
                return null;
            } else {
                if(index < paths.length - 1)
                    return ((MultiPartModel) model).getBoneByPath(paths, index + 1);
                return model;
            }
        } else {return null;}
    }

    public void setRenderTypeFor(String bonePath, RenderTypeBuilder builder) {
        SimpleModel model = getBoneByPath(bonePath);
        if(model != null) model.renderType(builder);
    }

    public boolean containsBone(String boneKey) {
        return bones.containsKey(boneKey);
    }

    public SimpleModel getBone(String boneKey) {
        return bones.getOrDefault(boneKey, null);
    }

    public Map<String, SimpleModel> getBoneMap() {
        return bones;
    }

    public void applyParentRenderTypeForBone(String bonePath) {
        SimpleModel model = getBoneByPath(bonePath);
        if(builder != null && model != null) {
            setRenderTypeFor(bonePath, builder);
        }
    }

    public void applyParentRenderTypeForAllBones() {
        if(builder != null) {
            for(String key : bones.keySet()) {
                SimpleModel b = bones.get(key);
                b.renderType(builder);
                if(b instanceof MultiPartModel multiPartModel)
                    multiPartModel.applyParentRenderTypeForAllBones();
            }
        }
    }

    public void translateBone(String boneKey, double x, double y, double z) {
        if(containsBone(boneKey))
            bones.get(boneKey).translate(x, y, z);
    }

    public void rotateXForBone(String boneKey, float xRot) {
        if(containsBone(boneKey))
            getBone(boneKey).rotateX(xRot);
    }

    public void rotateYForBone(String boneKey, float yRot) {
        if(containsBone(boneKey))
            getBone(boneKey).rotateY(yRot);
    }

    public void rotateZForBone(String boneKey, float zRot) {
        if(containsBone(boneKey))
            getBone(boneKey).rotateX(zRot);
    }

    public void addBone(SimpleModel model) {
        bones.put(model.key, model);
    }

    public void addAllBones(SimpleModel... models) {
        for(SimpleModel model : models) {
            addBone(model);
        }
    }

    public Set<String> getAllKeys() {
        return getBoneMap().keySet();
    }

    public void setStaticMovements(StaticMovements movements) {
        this.staticMovements = movements;
    }

    public void removeBone(String key) {
        bones.remove(key);
    }

    public void removeBones(String... keys) {
        for(String key : keys) {
            removeBone(key);
        }
    }

    public void removeAllBones() {
        bones.clear();
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource source, float x, float y, float z, int light, int overlay) {
        if(!shouldRender) return;
        if(builder == null) return;
        if(cacheType == null) cacheType = builder.build();
        boolean shouldPush = pose.clear();
        Matrix4f lastMatrix = null;
        if (shouldPush) {
            lastMatrix = pose.last().pose();
            pose.popPose();
        }
        pose.pushPose();
        if(useParentPose && lastMatrix != null) pose.mulPoseMatrix(lastMatrix);
        if (staticMovements != null) staticMovements.move(this);
        context.apply(pose);
        pose.translate(x, y, z);
        for(String key : bones.keySet()) {
            bones.get(key).shouldUseParentPose(true);
            bones.get(key).render(pose, source, x, y, z, light, overlay);
        }
        pose.popPose();
        if (shouldPush) {
            pose.pushPose();
            pose.mulPoseMatrix(lastMatrix);
        }
    }

    @Override
    public MultiPartModel clone() {
        MultiPartModel multiPartModel = new MultiPartModel(String.valueOf(this.key.toCharArray()));
        for(SimpleModel model1 : this.bones.values()) {
            multiPartModel.addBone(model1.clone());
        }
        return multiPartModel;
    }

    public interface StaticMovements {
        void move(MultiPartModel model);
    }
}
