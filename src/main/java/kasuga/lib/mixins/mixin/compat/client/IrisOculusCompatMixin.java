package kasuga.lib.mixins.mixin.compat.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.coderbot.iris.block_rendering.BlockRenderingSettings", remap = false)
public interface IrisOculusCompatMixin {
    @Accessor("useExtendedVertexFormat")
    public boolean kasugalib$getUseExtendedVertexFormat();

    @Accessor("useExtendedVertexFormat")
    public void kasugalib$setUseExtendedVertexFormat(boolean value);
}
