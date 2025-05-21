package kasuga.lib.core.client.block_bench_model.model;

import com.mojang.math.Vector3f;
import kasuga.lib.core.client.block_bench_model.json_data.Group;
import kasuga.lib.core.client.render.texture.Vec2f;
import lombok.Getter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
@Getter
public class BlockBenchGroup implements ModelElement, IModelGeometryPart {

    private final String name;
    private final UUID id;
    private final Group groupFile;
    private final Vector3f pivot, rotation;
    private final boolean render;
    private final HashMap<UUID, ModelElement> children;

    public BlockBenchGroup(Group group) {
        this.name = group.getName();
        this.groupFile = group;
        this.id = group.getId();
        this.pivot = group.getPivot();
        this.rotation = group.getRotation();
        this.render = group.isExport() && group.isVisibility();
        children = new HashMap<>();
    }

    public BlockBenchGroup() {
        name = "";
        id = null;
        groupFile = null;
        pivot = new Vector3f();
        rotation = new Vector3f();
        render = true;
        children = new HashMap<>();
    }

    public void addQuads(IModelBuilder<?> modelBuilder, ModelState modelTransform,
                         TransformContext transform, Vec2f resolution,
                         Function<Material, TextureAtlasSprite> spriteGetter) {
        if (!this.render) return;
        TransformContext myTransform = transform.transform(this.rotation, this.pivot);
        for (ModelElement child : children.values()) {
            child.addQuads(modelBuilder, modelTransform, myTransform, resolution, spriteGetter);
        }
    }

    public void addChild(ModelElement child) {
        children.put(child.getId(), child);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void addQuads(IModelConfiguration iModelConfiguration, IModelBuilder<?> iModelBuilder, ModelBakery modelBakery, Function<Material, TextureAtlasSprite> function, ModelState modelState, ResourceLocation resourceLocation) {
        // addQuads(iModelBuilder, modelState, new TransformContext(), );
    }
}
