package kasuga.lib.core.resource;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

public class CustomResourceReloadListener extends SimplePreparableReloadListener {

    public static final CustomResourceReloadListener INSTANCE = new CustomResourceReloadListener();

    @Getter
    private final HashMap<ResourceLocation, PackType> packs;

    private final HashMap<ResourceLocation, KasugaPackResource> cachedResources;

    @Getter
    private final HashMap<ResourceLocation, Stack<Consumer<KasugaPackResource>>> registerFunctions;

    private CustomResourceReloadListener() {
        packs = new HashMap<>();
        cachedResources = new HashMap<>();
        registerFunctions = new HashMap<>();
    }

    @Override
    protected Object prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return null;
    }

    @Override
    public void apply(Object pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        packs.forEach((key, value) -> {
            KasugaPackResource resource;
            if (!cachedResources.containsKey(key) || cachedResources.get(key) == null) {
                resource =
                        Resources.internalRegisterCustomPack(
                                value, key.getNamespace(), key.getPath()
                        );
                cachedResources.put(key, resource);
            } else {
                resource = cachedResources.get(key);
                Resources.updateCustomPack(resource);
            }
            Stack<Consumer<KasugaPackResource>> stack = this.registerFunctions.get(key);
            if (stack != null && !stack.isEmpty()) {
                while (!stack.isEmpty()) {
                    Consumer<KasugaPackResource> consumer = stack.pop();
                    consumer.accept(resource);
                }
            }
        });
    }
}
