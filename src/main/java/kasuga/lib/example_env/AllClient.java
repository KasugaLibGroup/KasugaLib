package kasuga.lib.example_env;

import kasuga.lib.core.client.model.BedrockModelLoader;
import kasuga.lib.core.client.model.anim_json.AnimationFile;
import kasuga.lib.core.client.model.model_json.UnbakedBedrockModel;
import kasuga.lib.core.client.world_overlay.WorldOverlay;
import kasuga.lib.core.client.world_overlay.WorldOverlayRenderer;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.example_env.client.world_overlay.TestOverlay;

public class AllClient {

    // public static final LazyRecomputable<AnimationFile> anim = AnimationFile.
            // fromFile(AllExampleElements.REGISTRY.asResource("animations/model.animation.json"));

    // public static final LazyRecomputable<UnbakedBedrockModel> model = BedrockModelLoader.fromFile(
            // AllExampleElements.REGISTRY.asResource("block/test/test_model_complicate"));

    public static final WorldOverlay overlay = WorldOverlayRenderer.add(new TestOverlay());
    public static void invoke(){}
}
