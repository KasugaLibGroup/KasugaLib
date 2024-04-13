package kasuga.lib.core.create;

import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TrackStateGenerator extends SpecialBlockStateGen {
    private final RotationGenerator xGen, yGen;
    private final List<ModelBuilderContext> parentContext;
    private final String parentPath;
    public TrackStateGenerator
            (@Nullable final RotationGenerator xGen, @Nullable final RotationGenerator yGen, @Nonnull final String parentPath) {
        this.xGen = xGen;
        this.yGen = yGen;
        parentContext = new ArrayList<>();
        this.parentPath = parentPath;
    }
    @Override
    protected int getXRotation(BlockState state) {
        return xGen != null ? xGen.apply(state) : 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return yGen != null ? yGen.apply(state) : 0;
    }

    public void modelBuilder(ModelBuilderContext context) {
        parentContext.add(context);
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        TrackMaterial material = ((TrackBlock) ctx.getEntry()).getMaterial();
        String emptyModel = "block/air";
        TrackShape shape = state.getValue(TrackBlock.SHAPE);
        if (shape == TrackShape.NONE) return prov.models().getExistingFile(prov.modLoc(emptyModel));
        BlockModelProvider provider = prov.models();
        return buildModel(material, provider, shape, parentContext);
    }

    private BlockModelBuilder buildModel(TrackMaterial material, BlockModelProvider provider, TrackShape shape, List<ModelBuilderContext> contexts) {
        BlockModelBuilder builder = null;
        String prefix = "block/";
        for (ModelBuilderContext context : contexts) {
            if (builder == null) {
                if (context.type == ModelActionType.PARENT)
                    builder = provider.withExistingParent(prefix + parentPath + "/" + shape.getModel(),
                            new ResourceLocation(context.attr.getNamespace(), prefix + "/" + context.attr.getPath() + "/" + shape.getModel()));
            } else {
                if (context.type == ModelActionType.PARENT)
                    builder = builder.texture(context.boneName, context.attr == null ? material.particle : context.attr);
            }
        }
        return builder;
    }

    public interface RotationGenerator {int apply(BlockState state);}

    public record ModelBuilderContext(ModelActionType type, String boneName, ResourceLocation attr){
        public static ModelBuilderContext of(ModelActionType type, String boneName, ResourceLocation location) {
            return new ModelBuilderContext(type, boneName, location);
        }
    }

    public enum ModelActionType {
        PARENT,
        TEXTURE;
    }

    public static class Builder {
        private RotationGenerator xGen, yGen;
        private List<ModelBuilderContext> contexts;
        private String parentPath;
        protected Builder(){}

        public static Builder of(String parentPath){
            Builder builder = new Builder();
            builder.parentPath = parentPath;
            builder.contexts = new ArrayList<>();
            return builder;
        }

        public Builder xRotation(RotationGenerator xGen) {
            this.xGen = xGen;
            return this;
        }

        public Builder yRotation(RotationGenerator yGen) {
            this.yGen = yGen;
            return this;
        }

        public Builder addModelContext(ModelBuilderContext context) {
            this.contexts.add(context);
            return this;
        }

        public TrackStateGenerator build() {
            TrackStateGenerator generator = new TrackStateGenerator(xGen, yGen, parentPath);
            generator.parentContext.addAll(contexts);
            return generator;
        }
    }
}
