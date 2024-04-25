package kasuga.lib.core.client.gui.intergration.javascript;

import kasuga.lib.core.client.gui.KasugaScreen;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.FileUtils;
import org.graalvm.polyglot.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class JavascriptScreen {
    public JavascriptContext guiContext;
    public KasugaScreen screen;

    public JavascriptScreen(ResourceLocation codePath) throws IOException {
        Resource resource = Resources.getResource(codePath);
        BufferedReader reader = resource.openAsReader();
        String line;
        StringBuilder sb = new StringBuilder();
        while((line = reader.readLine()) != null)sb.append(line+"\n");
        String code = sb.toString();
        guiContext = new JavascriptContext(code);
    }

    public void attach(){
        Screen screen = KasugaScreen.screenWithTickFunction(this.guiContext.getContainer().getRoot(),()->{
            Value cb;
            while((cb = this.guiContext.getNative().callbackQueue.poll())!=null){
                cb.executeVoid();
            };
            return null;
        });
        Minecraft.getInstance().setScreen(screen);
        guiContext.run();
    }

    public void detach(){

    }

    public static void test() throws IOException{
        if(Minecraft.getInstance().screen != null)
            return;
        JavascriptScreen screen = new JavascriptScreen(new ResourceLocation("kasuga_lib","guis/test_screen.js"));
        screen.attach();
    }
}
