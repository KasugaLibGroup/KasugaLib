package kasuga.lib.mixins.mixin.resources;

import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraftforge.resource.DelegatingPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.zip.ZipFile;

@Mixin(DelegatingPackResources.class)
public interface DelegatingPackResourcesMixin {
    @Accessor
    public List<PackResources> getDelegates();
}
