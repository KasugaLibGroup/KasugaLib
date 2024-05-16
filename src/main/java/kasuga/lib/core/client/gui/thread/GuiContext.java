package kasuga.lib.core.client.gui.thread;

import kasuga.lib.core.client.gui.KasugaScreen;
import kasuga.lib.core.client.gui.KasugaTimer;
import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.context.RenderContext;
import kasuga.lib.core.client.gui.intergration.javascript.JavascriptGuiBinding;
import kasuga.lib.core.client.gui.intergration.javascript.JavascriptPlatformRuntime;
import kasuga.lib.core.client.gui.runtime.PlatformModule;
import kasuga.lib.core.client.gui.runtime.PlatformRuntime;
import kasuga.lib.core.client.gui.style.Styles;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;
import org.graalvm.polyglot.Source;
import org.lwjgl.util.yoga.Yoga;

import java.util.Map;
import java.util.WeakHashMap;

public class GuiContext {
    PlatformRuntime<? extends PlatformModule> runtime;

    GuiAttachTarget attachedTargets = new GuiAttachTarget();

    Node root = new Node();

    KasugaTimer timer = new KasugaTimer();
    private boolean closable;

    private final JavascriptGuiBinding guiBinding;

    Map<Object, Pair<Float,Float>> sizes = new WeakHashMap<>();

    static Pair<Float,Float> INFINITY_SIZE = Pair.of(Yoga.YGUndefined,Yoga.YGUndefined);
    private boolean closed = false;
    public GuiContext(GuiThread thread){
        this.runtime = thread.createRuntime();
        this.guiBinding = new JavascriptGuiBinding((JavascriptPlatformRuntime) runtime,root);
        this.runtime.bindContext(this);
        root.style().addStyle(Styles.WIDTH.create("100%"));
        root.style().addStyle(Styles.HEIGHT.create("100%"));
    }

    public boolean closable(){
        return closable;
    }

    public Node getRootNode(){
        return root;
    }

    public void tick(){
        if(closed)
            return;
        if(attachedTargets.tickClosable() > 20){
            closable = true;
            return;
        }
        timer.onTick();
        this.runtime.tick();
    }

    public void renderPreTick(){
        if(closed)
            return;
        for (Object attachedTarget : this.attachedTargets) {
            renderPreTick(attachedTarget);
        }
    }

    public void renderPreTick(Object target){
        if(closed || !root.getLocator().alive(target))
            return;
        root.applyStyles();
        Pair<Float,Float> size = sizes.getOrDefault(target,INFINITY_SIZE);
        root.getLocator().calculateLayout(target,size.getFirst(),size.getSecond());
        root.checkShouldReLayout(target);
        root.renderPreTick(target);
    }

    public void execute(String code){
        this.runtime.run(code);
    }

    public void render(RenderContext renderContext){
        if(closed)
            return;
        root.dispatchRender(renderContext);
    }

    public JavascriptGuiBinding getBinding() {
        return guiBinding;
    }

    public void run(ResourceLocation main){
        this.runtime.importModule(main);
    }

    public void runSource(Source source){
        this.runtime.run(source);
    }

    public void close() {
        closable = true;
        closed = true;
        this.runtime.close();
        this.attachedTargets.detach();
        this.guiBinding.close();
        this.root.close();
    }

    public KasugaTimer getTimer() {
        return timer;
    }

    public GuiAttachTarget getAttachedTargets() {
        return attachedTargets;
    }

    public void runAsync(ResourceLocation mainFile) {
        timer.register(KasugaTimer.TimerType.TIMEOUT,()->run(mainFile),1);
    }

    public boolean closed() {
        return closed;
    }

    public void size(Object object,float width, float height) {
        this.sizes.put(object,Pair.of(width,height));
    }

    public void createSource(Object source) {
        this.root.getLocator().addSource(source);
    }

    public void removeSource(Object source){
        this.root.getLocator().removeSource(source);
    }
}
