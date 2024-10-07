package kasuga.lib.mixins.mixin.resources;

import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraftforge.resource.DelegatingResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.zip.ZipFile;

@Mixin(value = DelegatingResourcePack.class, remap = false)
public interface DelegatingResourcePackMixin {
    @Accessor
    public List<PackResources> getDelegates();
}
