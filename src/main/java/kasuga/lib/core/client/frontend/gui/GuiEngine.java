package kasuga.lib.core.client.frontend.gui;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.assets.TextureAssetProvider;
import kasuga.lib.core.client.frontend.dom.nodes.NodeTypeRegistry;
import kasuga.lib.core.client.frontend.dom.registration.DOMPriorityRegistry;
import kasuga.lib.core.client.frontend.gui.canvas.CanvasManager;
import kasuga.lib.core.client.frontend.gui.nodes.AllGuiNodes;
import kasuga.lib.core.client.frontend.gui.nodes.GuiCanvasNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.gui.styles.AllGuiStyles;
import kasuga.lib.core.client.frontend.gui.styles.GuiStyleRegistry;
import kasuga.lib.core.client.frontend.rendering.ImageProviders;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class GuiEngine {
    public final DOMPriorityRegistry domRegistry = new DOMPriorityRegistry();

    public final NodeTypeRegistry<GuiDomNode> nodeTypeRegistry = new NodeTypeRegistry<>();

    public final GuiStyleRegistry styleRegistry = new GuiStyleRegistry();

    public final CanvasManager canvasManager = new CanvasManager();

    public void init(){
        KasugaLib.STACKS.JAVASCRIPT.registry.register(new ResourceLocation("kasuga_lib","gui"),domRegistry);
        TextureAssetProvider.init();
        AllGuiStyles.register(styleRegistry);
        AllGuiNodes.register(nodeTypeRegistry);
        ImageProviders.init();
    }

    public HashSet<GuiInstance> instances = new HashSet<>();

    public GuiInstance create(ResourceLocation location){
        GuiInstance instance = new GuiInstance(location);
        this.instances.add(instance);
        return instance;
    }

    public void closeInstance(GuiInstance instance){
        this.instances.remove(instance);
    }

    public void renderTick() {
        for (GuiInstance instance : instances) {
            instance.getContext().ifPresent(GuiContext::renderTick);
        }
    }

    public HashMap<UUID, GuiInstance> localInstances = new HashMap<>();

    public GuiInstance create(UUID id, ResourceLocation location){
        GuiInstance instance = create(location);
        localInstances.put(id, instance);
        return instance;
    }

    public Optional<GuiInstance> getInstanceById(UUID id) {
        return Optional.ofNullable(localInstances.get(id));
    }

    public HashMap<UUID, GuiInstance> getAllInstances() {
        return localInstances;
    }
}
