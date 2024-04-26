package kasuga.lib.core.client.gui.thread;

import kasuga.lib.core.client.gui.KasugaTimer;
import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.context.RenderContext;
import kasuga.lib.core.client.gui.intergration.javascript.JavascriptGuiBinding;
import kasuga.lib.core.client.gui.intergration.javascript.JavascriptPlatformRuntime;
import kasuga.lib.core.client.gui.runtime.PlatformModule;
import kasuga.lib.core.client.gui.runtime.PlatformRuntime;
import net.minecraft.resources.ResourceLocation;

public class GuiContext {
    PlatformRuntime<? extends PlatformModule> runtime;

    GuiAttachTarget attachedTargets = new GuiAttachTarget();

    Node root = new Node();

    KasugaTimer timer = new KasugaTimer();
    private boolean closable;

    private final JavascriptGuiBinding guiBinding;
    private int width;
    private int height;
    private boolean closed = false;
    private boolean ready;

    public GuiContext(GuiThread thread){
        this.runtime = thread.createRuntime();
        this.guiBinding = new JavascriptGuiBinding((JavascriptPlatformRuntime) runtime,root);
        this.runtime.bindContext(this);
    }

    public boolean closable(){
        return closable;
    }

    public Node getRootNode(){
        return root;
    }

    public void setSize(int width,int height){
        this.width = width;
        this.height = height;
        root.getLocatorNode().setWidth(width);
        root.getLocatorNode().setHeight(height);
        renderPreTick();
    }

    public void tick(){
        if(closed)
            return;
        if(attachedTargets.tickClosable() > 20){
            closable = true;
            return;
        }
        timer.onTick();
    }

    public void renderPreTick(){
        if(closed)
            return;
        root.applyStyles();
        root.getLocatorNode().calculateLayout(width,height);
        root.checkShouldReLayout();
        root.renderPreTick();
        if(width != 0 && height !=0){
            this.ready = true;
        }
    }

    public void execute(String code){
        this.runtime.run(code);
    }

    public void render(RenderContext renderContext){
        if(closed)
            return;
        if(!ready){
            // TODO: Display the waiting page
            return;
        }
        root.dispatchRender(renderContext);
    }

    public JavascriptGuiBinding getBinding() {
        return guiBinding;
    }

    public void run(ResourceLocation main){
        this.runtime.importModule(main);
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
}
