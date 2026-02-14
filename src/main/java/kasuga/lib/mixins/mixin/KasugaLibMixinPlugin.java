package kasuga.lib.mixins.mixin;

import kasuga.lib.core.compat.AllCompatMods;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class KasugaLibMixinPlugin implements IMixinConfigPlugin {

    protected final HashMap<String, Predicate<Void>> mixinConditions = new HashMap<>();

    KasugaLibMixinPlugin(){
        mixinConditions.put("IrisOculusCompatMixin", v -> AllCompatMods.isIrisOculusPresent());
    }

    @Override
    public void onLoad(String s) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String s, String mixinClassName) {
        String simpleName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        return Optional.ofNullable(mixinConditions.get(simpleName))
                .map(condition -> condition.test(null))
                .orElse(
                        Optional.ofNullable(mixinConditions.get(mixinClassName))
                                .map(condition -> condition.test(null))
                                .orElse(true)
                );
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}
