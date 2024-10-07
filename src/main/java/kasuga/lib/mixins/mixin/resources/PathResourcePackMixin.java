package kasuga.lib.mixins.mixin.resources;

import net.minecraft.server.packs.FilePackResources;
import net.minecraftforge.resource.PathResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.file.Path;
import java.util.zip.ZipFile;

@Mixin(value = PathResourcePack.class,remap = false)
public interface PathResourcePackMixin {
    @Invoker
    public Path invokeResolve(String... paths);
}
