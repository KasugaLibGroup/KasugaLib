package kasuga.lib.example_env;

import kasuga.lib.core.client.model.anim_json.AnimationFile;
import kasuga.lib.core.util.LazyRecomputable;

public class AllClient {

    public static final LazyRecomputable<AnimationFile> anim = AnimationFile.
            fromFile(AllExampleElements.testRegistry.asResource("animations/model.animation.json"));

    public static void invoke(){}
}
