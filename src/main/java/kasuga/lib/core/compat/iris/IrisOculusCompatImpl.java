package kasuga.lib.core.compat.iris;

import kasuga.lib.mixins.mixin.compat.client.IrisOculusCompatMixin;
import net.coderbot.iris.block_rendering.BlockRenderingSettings;
import net.coderbot.iris.shadows.ShadowRenderingState;

public class IrisOculusCompatImpl implements IrisOculusCompat {
    public IrisOculusCompatImpl() {}

    @Override
    public boolean isRenderingShadow() {
        return ShadowRenderingState.areShadowsCurrentlyBeingRendered();
    }

    protected Boolean isUsingExtendedVertexFormat;

    @Override
    public void pushExtendedVertexFormat(boolean newValue) {
        if(isUsingExtendedVertexFormat != null)
            throw new IllegalStateException("Already in extended vertex bypass mode!");
        isUsingExtendedVertexFormat = ((IrisOculusCompatMixin) BlockRenderingSettings.INSTANCE).kasugalib$getUseExtendedVertexFormat();
        ((IrisOculusCompatMixin) BlockRenderingSettings.INSTANCE).kasugalib$setUseExtendedVertexFormat(newValue);
    }

    @Override
    public void popExtendedVertexFormat() {
        if(isUsingExtendedVertexFormat == null)
            throw new IllegalStateException("Not in extended vertex bypass mode!");
        ((IrisOculusCompatMixin) BlockRenderingSettings.INSTANCE).kasugalib$setUseExtendedVertexFormat(isUsingExtendedVertexFormat.booleanValue());
        isUsingExtendedVertexFormat = null;
    }
}
