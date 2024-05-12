package kasuga.lib.core.client.animation;

import interpreter.Code;
import interpreter.compute.data.Namespace;
import interpreter.compute.data.functions.SingleParamFunction;
import interpreter.compute.data.functions.TripleParamFunction;
import interpreter.compute.infrastructure.Formula;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.animation.data.Animation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;

public class Constants {
    private static int tick = 0;
    private static boolean shouldAct = false;
    private static final HashMap<Integer, Animation> animations = new HashMap<>();
    public static final Namespace ANIM_ROOT_NAMESPACE = new Namespace(Code.ROOT_NAMESPACE);
    public static final TripleParamFunction animTranslate = ANIM_ROOT_NAMESPACE.register3Param("translate", (x, y, z) -> 0f);
    public static final TripleParamFunction animScale = ANIM_ROOT_NAMESPACE.register3Param("scale", (x, y, z) -> 0f);
    public static final SingleParamFunction animXRot = ANIM_ROOT_NAMESPACE.register1Param("x_rot", (x) -> 0f);
    public static final SingleParamFunction animYRot = ANIM_ROOT_NAMESPACE.register1Param("y_rot", (y) -> 0f);
    public static final SingleParamFunction animZRot = ANIM_ROOT_NAMESPACE.register1Param("z_rot", (z) -> 0f);
    public static final SingleParamFunction animXRotRad = ANIM_ROOT_NAMESPACE.register1Param("x_rot_rad", (z) -> 0f);
    public static final SingleParamFunction animYRotRad = ANIM_ROOT_NAMESPACE.register1Param("y_rot_rad", (z) -> 0f);
    public static final SingleParamFunction animZRotRad = ANIM_ROOT_NAMESPACE.register1Param("z_rot_rad", (z) -> 0f);
    public static final SingleParamFunction animPointTo = ANIM_ROOT_NAMESPACE.register1Param("point_to", (a) -> 0f);
    public static Namespace root() {
        return ANIM_ROOT_NAMESPACE;
    }

    public static void stackAnimateIn(Animation animation) {
        animations.put(KasugaLib.STACKS.random().nextInt(), animation);
    }

    public static boolean haAnimation(Animation animation) {
        return animations.containsValue(animation);
    }

    public static void removeAnimation(Animation animation) {
        Integer i = -1;
        for (Integer key : animations.keySet()) {
            if (animations.get(key).equals(animation)) {
                i = key;
                break;
            }
        }
        animations.remove(i);
    }

    public static int tick(){return tick;}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(!shouldAct) return;
        if(tick >= 19) tick = 0;
        else tick++;
        animations.forEach((integer, animation) -> animation.tick());
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
