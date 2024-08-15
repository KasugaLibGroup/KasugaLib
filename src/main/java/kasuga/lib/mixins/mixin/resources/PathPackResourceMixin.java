package kasuga.lib.mixins.mixin.resources;

import net.minecraft.server.packs.FilePackResources;
import net.minecraftforge.resource.PathPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.file.Path;
import java.util.zip.ZipFile;

@Mixin(value = PathPackResources.class,remap = false)
public interface PathPackResourceMixin {
    @Invoker
    public Path invokeResolve(String... paths);
}
