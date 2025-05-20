package kasuga.lib.core.client.animation.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import interpreter.compute.data.Namespace;
import kasuga.lib.core.client.animation.Constants;
import kasuga.lib.core.client.animation.data.anchor.Anchor;
import kasuga.lib.core.client.animation.data.anchor.AnchorsGroup;
import kasuga.lib.core.client.animation.data.bones.BonesGroup;
import kasuga.lib.core.client.animation.data.timer.TimeLineGroup;
import kasuga.lib.core.client.animation.data.trigger.TriggerGroup;
import kasuga.lib.core.client.animation.infrastructure.IAnchor;
import kasuga.lib.core.client.animation.infrastructure.MappingLayer;
import kasuga.lib.core.client.render.PoseContext;
import kasuga.lib.core.client.render.model.SimpleModel;
import kasuga.lib.core.resource.Resources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@OnlyIn(Dist.CLIENT)
public class Animation {
    private final String key;
    private final ResourceLocation location;
    private final Namespace namespace;
    private final MappingLayer mappingLayer;
    private final TriggerGroup triggerGroup;
    private final TimeLineGroup timeLineGroup;
    private final BonesGroup bonesGroup;
    private final AnchorsGroup anchorsGroup;
    public Animation(String name, Namespace namespace, ResourceLocation location) {
        this.key = name;
        this.location = location;
        this.namespace = namespace;
        mappingLayer = new MappingLayer();
        mappingLayer.buildMapping();
        triggerGroup = new TriggerGroup(namespace);
        timeLineGroup = new TimeLineGroup(namespace);
        bonesGroup = new BonesGroup(this, namespace);
        anchorsGroup = new AnchorsGroup(this, namespace);
    }

    public void decode(JsonObject root) {
        if (root.has("anchor"))
            anchorsGroup.decodeAnchors(root.getAsJsonObject("anchor"));
        if (root.has("trigger"))
            triggerGroup.decode(root.getAsJsonObject("trigger"));
        if (root.has("timer"))
            timeLineGroup.decode(root.getAsJsonObject("timer"));
        if (root.has("actor"))
            bonesGroup.decodeBones(root.getAsJsonObject("actor"));
        init();
    }

    public static Map<String, Animation> decode(Namespace namespace, ResourceLocation location) throws IOException {
        Resource resources = Resources.getResource(location);
        JsonObject fileRoot = JsonParser.parseReader(Resources.openAsJson(resources)).getAsJsonObject();
        if(!fileRoot.has("animation")) return new HashMap<>();
        HashMap<String, Animation> result = new HashMap<>();
        JsonObject root = fileRoot.getAsJsonObject("animation");
        for(Map.Entry<String, JsonElement> entry : root.entrySet()) {
            Animation animation = new Animation(entry.getKey(), namespace, location);
            animation.decode(entry.getValue().getAsJsonObject());
            result.put(entry.getKey(), animation);
        }
        return result;
    }

    public SimpleModel getModel() {
        return mappingLayer.getModel();
    }

    public static Optional<Animation> decode(Namespace namespace, ResourceLocation location, String name) {
        try {
            Map<String, Animation> map = decode(namespace, location);
            if(map.containsKey(name))
                return Optional.of(map.get(name));
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public static Optional<Animation> decode(ResourceLocation location, String name) {
        try {
            Map<String, Animation> map = decode(Constants.root().clone(), location);
            if(map.containsKey(name))
                return Optional.of(map.get(name));
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public void init() {
        triggerGroup.init();
        timeLineGroup.init();
        bonesGroup.init();
        anchorsGroup.init();
        Constants.stackAnimateIn(this);
    }

    public void action() {
        triggerGroup.action();
        timeLineGroup.action();
        for (Map.Entry<String, SimpleModel> entry : mappingLayer.getMapping().entrySet()) {
            String key = entry.getKey();
            SimpleModel model = entry.getValue();
            if (bonesGroup.containsMovements(key)) {
                for (PoseContext.Action action : bonesGroup.getMovement(key).getAllActions()) {
                    model.addAction(action);
                }
            }
        }
    }

    public void tick() {
        timeLineGroup.tick();
    }

    public void assign(String codec, float value) {
        namespace.assign(codec, value);
    }

    public void assign(Entity entity, float partial) {
        assign("x", (float) entity.position().x());
        assign("y", (float) entity.position().y());
        assign("z", (float) entity.position().z());
        assign("x_rot", entity.getXRot());
        assign("y_rot", entity.getYRot());
        assign("time", entity.getLevel().getDayTime());
        assign("tick", Constants.tick());
        assign("partial", partial);
    }

    public BonesGroup getBonesGroup() {
        return bonesGroup;
    }
    public boolean containsBoneMovement(String key) {
        return bonesGroup.containsMovements(key);
    }

    public AnchorsGroup getAnchors() {
        return anchorsGroup;
    }

    public boolean containsAnchor(String key) {
        return anchorsGroup.containsAnchor(key);
    }

    public boolean containsAsAnchor(String codec) {
        return containsAnchor(codec) || containsBoneMovement(codec);
    }
    public IAnchor getAsAnchor(String codec) {
        if (containsAnchor(codec)) {
            return anchorsGroup.getAnchor(codec);
        } else if (containsBoneMovement(codec)) {
            return bonesGroup.getMovement(codec);
        } else {
            return new Anchor(this, namespace, "invalid");
        }
    }


    public void assign(BlockEntity tile, float partial) {
        assign("x", (float) tile.getBlockPos().getX());
        assign("y", (float) tile.getBlockPos().getY());
        assign("z", (float) tile.getBlockPos().getZ());
        if(tile.getBlockState().hasProperty(BlockStateProperties.FACING))
            assign("x_rot", -tile.getBlockState().getValue(BlockStateProperties.FACING).getOpposite().toYRot());
        else
            assign("x_rot", 0);
        assign("y_rot", 0f);
        if(tile.hasLevel())
            assign("time", tile.getLevel().getDayTime());
        else
            assign("time", 0);
        assign("tick", Constants.tick());
        assign("partial", partial);
    }

    public void loadModel(SimpleModel model) {
        mappingLayer.rebuildMapping(model);
    }

    public Namespace getNamespace() {
        return namespace;
    }


    public ResourceLocation getLocation() {
        return location;
    }
    public String getName() {
        return key;
    }

    public Animation clone() {
        return decode(this.location, this.key).orElse(null);
    }
}
