package kasuga.lib.core.compat.iris;

import kasuga.lib.mixins.mixin.compat.client.IrisOculusCompatMixin;
//import net.coderbot.iris.block_rendering.BlockRenderingSettings;
//import net.coderbot.iris.shadows.ShadowRenderingState;

public class IrisOculusCompatImpl implements IrisOculusCompat {
    public IrisOculusCompatImpl() {}

    @Override
    public boolean isRenderingShadow() {
        // todo 20250907 commit these line because net.coderbot.iris.block_rendering.BlockRenderingSettings & net.coderbot.iris.shadows.ShadowRenderingState is not exist in oculus 1.8.0 on 1.20.1
//        return ShadowRenderingState.areShadowsCurrentlyBeingRendered();
        return false;
    }

    protected Boolean isUsingExtendedVertexFormat;

    @Override
    public void pushExtendedVertexFormat(boolean newValue) {
        // todo 20250907 commit these line because net.coderbot.iris.block_rendering.BlockRenderingSettings & net.coderbot.iris.shadows.ShadowRenderingState is not exist in oculus 1.8.0 on 1.20.1
//        if(isUsingExtendedVertexFormat != null)
//            throw new IllegalStateException("Already in extended vertex bypass mode!");
//        isUsingExtendedVertexFormat = ((IrisOculusCompatMixin) BlockRenderingSettings.INSTANCE).kasugalib$getUseExtendedVertexFormat();
//        ((IrisOculusCompatMixin) BlockRenderingSettings.INSTANCE).kasugalib$setUseExtendedVertexFormat(newValue);
    }

    @Override
    public void popExtendedVertexFormat() {
        // todo 20250907 commit these line because net.coderbot.iris.block_rendering.BlockRenderingSettings & net.coderbot.iris.shadows.ShadowRenderingState is not exist in oculus 1.8.0 on 1.20.1
//        if(isUsingExtendedVertexFormat == null)
//            throw new IllegalStateException("Not in extended vertex bypass mode!");
//        ((IrisOculusCompatMixin) BlockRenderingSettings.INSTANCE).kasugalib$setUseExtendedVertexFormat(isUsingExtendedVertexFormat.booleanValue());
//        isUsingExtendedVertexFormat = null;
    }
}
