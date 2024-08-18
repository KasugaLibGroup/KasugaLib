package kasuga.lib.mixins.mixin.resources;

import net.minecraft.server.packs.FilePackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.zip.ZipFile;

@Mixin(FilePackResources.class)
public interface FilePackResourceMixin {
    @Invoker
    public ZipFile invokeGetOrCreateZipFile();
}
