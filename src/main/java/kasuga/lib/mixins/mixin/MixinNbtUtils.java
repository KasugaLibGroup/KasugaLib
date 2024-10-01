package kasuga.lib.mixins.mixin;

import kasuga.lib.core.base.UnModeledBlockProperty;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NbtUtils.class)
public class MixinNbtUtils {

    @Redirect(method = "readBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/StateDefinition;getProperty(Ljava/lang/String;)Lnet/minecraft/world/level/block/state/properties/Property;"), remap = false)
    private static Property<?> doGetProperty(StateDefinition instance, String pPropertyName) {
        if (instance.getProperty(pPropertyName) instanceof UnModeledBlockProperty<?,?>)
            return null;
        return instance.getProperty(pPropertyName);
    }
}
