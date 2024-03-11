package kasuga.lib.core.client.animation;

import kasuga.lib.codes.Code;
import kasuga.lib.codes.compute.data.Namespace;
import kasuga.lib.codes.compute.data.functions.SingleParamFunction;
import kasuga.lib.codes.compute.data.functions.TripleParamFunction;
import kasuga.lib.core.client.animation.data.Animation;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;

public class Constants {
    private static int tick = 0;
    private static boolean shouldAct = false;
    private static final HashSet<Animation> animations = new HashSet<>();
    public static final Namespace ANIM_ROOT_NAMESPACE = new Namespace(Code.ROOT_NAMESPACE);
    public static final TripleParamFunction animTranslate = ANIM_ROOT_NAMESPACE.register3Param("translate", (x, y, z) -> 0f);
    public static final SingleParamFunction animXRot = ANIM_ROOT_NAMESPACE.register1Param("x_rot", (x) -> 0f);
    public static final SingleParamFunction animYRot = ANIM_ROOT_NAMESPACE.register1Param("y_rot", (y) -> 0f);
    public static final SingleParamFunction animZRot = ANIM_ROOT_NAMESPACE.register1Param("z_rot", (z) -> 0f);
    public static final SingleParamFunction animXRotRad = ANIM_ROOT_NAMESPACE.register1Param("x_rot_rad", (z) -> 0f);
    public static final SingleParamFunction animYRotRad = ANIM_ROOT_NAMESPACE.register1Param("y_rot_rad", (z) -> 0f);
    public static final SingleParamFunction animZRotRad = ANIM_ROOT_NAMESPACE.register1Param("z_rot_rad", (z) -> 0f);
    public static Namespace root() {
        return ANIM_ROOT_NAMESPACE;
    }

    public static void stackAnimateIn(Animation animation) {
        animations.add(animation);
    }

    public static boolean haAnimation(Animation animation) {
        return animations.contains(animation);
    }

    public static void removeAnimation(Animation animation) {
        animations.remove(animation);
    }

    public static int tick(){return tick;}
    public void invoke() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(!shouldAct) return;
        if(tick >= 19) tick = 0;
        else tick++;
        animations.forEach(Animation::tick);
    }

    @SubscribeEvent
    public static void onAnimStart(ClientPlayerNetworkEvent.LoggingIn event) {
        shouldAct = true;
    }

    @SubscribeEvent
    public static void onAnimStop(ClientPlayerNetworkEvent.LoggingOut event) {
        shouldAct = false;
        tick = 0;
    }
}
